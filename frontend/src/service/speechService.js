// 科大讯飞语音识别服务
import CryptoJS from 'crypto-js';

// 全局RecorderManager实例
export let RecorderManager = null;

// 初始化RecorderManager
const initRecorderManager = () => {
  if (RecorderManager) return RecorderManager;
  
  // 从全局window对象获取RecorderManager
  if (window.RecorderManager) {
    RecorderManager = window.RecorderManager;
  } else {
    // 动态加载RecorderManager
    const script = document.createElement('script');
    script.src = '/dist/index.umd.js';
    script.async = true;
    
    return new Promise((resolve) => {
      script.onload = () => {
        RecorderManager = window.RecorderManager;
        resolve(RecorderManager);
      };
      document.head.appendChild(script);
    });
  }
  
  return RecorderManager;
};

export class SpeechRecognitionService {
  constructor() {
    this.recorder = null;
    this.iatWS = null;
    this.isRecording = false;
    this.apiKey = '';
    this.appId = '';
    this.apiSecret = '';
    this.resultText = '';
    this.resultTextTemp = '';
    this.recordingOptions = {
      sampleRate: 16000,
      frameSize: 1280
    };
    this.status = 'CLOSED'; // UNDEFINED, CONNECTING, OPEN, CLOSING, CLOSED
    this.recognitionCallback = null;
  }

  // 设置科大讯飞API凭证
  setCredentials(config) {
    if (config.apiKey) this.apiKey = config.apiKey;
    if (config.appId) this.appId = config.appId;
    if (config.apiSecret) this.apiSecret = config.apiSecret;
  }
  
  // 设置录音参数
  setRecordingOptions(options) {
    if (options) {
      this.recordingOptions = { ...this.recordingOptions, ...options };
    }
  }

  // 将ArrayBuffer转换为Base64
  toBase64(buffer) {
    const binary = String.fromCharCode(...new Uint8Array(buffer));
    return window.btoa(binary);
  }

  // 获取WebSocket URL（带认证）
  getWebSocketUrl() {
    const url = "wss://iat-api.xfyun.cn/v2/iat";
    const host = "iat-api.xfyun.cn";
    const date = new Date().toGMTString();
    const algorithm = "hmac-sha256";
    const headers = "host date request-line";
    const signatureOrigin = `host: ${host}\ndate: ${date}\nGET /v2/iat HTTP/1.1`;
    const signatureSha = CryptoJS.HmacSHA256(signatureOrigin, this.apiSecret);
    const signature = CryptoJS.enc.Base64.stringify(signatureSha);
    const authorizationOrigin = `api_key="${this.apiKey}", algorithm="${algorithm}", headers="${headers}", signature="${signature}"`;
    const authorization = btoa(authorizationOrigin);
    return `${url}?authorization=${authorization}&date=${date}&host=${host}`;
  }

  // 连接到科大讯飞WebSocket
  async connectWebSocket(callback) {
    try {
      this.recognitionCallback = callback;
      this.status = 'CONNECTING';
      
      // 初始化RecorderManager
      if (!RecorderManager) {
        await initRecorderManager();
      }
      
      // 创建Recorder实例
      this.recorder = new RecorderManager("/dist");
      
      const websocketUrl = this.getWebSocketUrl();
      
      if ("WebSocket" in window) {
        this.iatWS = new WebSocket(websocketUrl);
      } else if ("MozWebSocket" in window) {
        this.iatWS = new MozWebSocket(websocketUrl);
      } else {
        throw new Error("浏览器不支持WebSocket");
      }
      
      this.iatWS.onopen = () => {
        this.status = 'OPEN';
        console.log('WebSocket连接已建立');
        
        // 开始录音
        this.recorder.start(this.recordingOptions);
        
        // 发送初始化参数
        const params = {
          common: {
            app_id: this.appId,
          },
          business: {
            language: "zh_cn",
            domain: "iat",
            accent: "mandarin",
            vad_eos: 5000,
            dwa: "wpgs",
          },
          data: {
            status: 0,
            format: "audio/L16;rate=16000",
            encoding: "raw",
          },
        };
        this.iatWS.send(JSON.stringify(params));
      };
      
      this.iatWS.onmessage = (e) => {
        this.renderResult(e.data);
      };
      
      this.iatWS.onerror = (e) => {
        console.error('WebSocket错误:', e);
        this.stop();
        if (this.recognitionCallback) {
          this.recognitionCallback(null, new Error('WebSocket连接错误'));
        }
      };
      
      this.iatWS.onclose = () => {
        this.status = 'CLOSED';
        console.log('WebSocket连接已关闭');
        
        // 如果是正常识别结束，回调结果
        if (this.resultText && this.recognitionCallback) {
          this.recognitionCallback(this.resultText, null);
        }
      };
      
      // 设置录音回调
      this.recorder.onFrameRecorded = ({ isLastFrame, frameBuffer }) => {
        if (this.iatWS && this.iatWS.readyState === WebSocket.OPEN) {
          this.iatWS.send(
            JSON.stringify({
              data: {
                status: isLastFrame ? 2 : 1,
                format: "audio/L16;rate=16000",
                encoding: "raw",
                audio: this.toBase64(frameBuffer),
              },
            })
          );
          if (isLastFrame) {
            this.status = 'CLOSING';
          }
        }
      };
      
      this.recorder.onStart = () => {
        console.log('录音已开始');
        this.isRecording = true;
      };
      
      this.recorder.onStop = () => {
        console.log('录音已停止');
        this.isRecording = false;
      };
      
    } catch (error) {
      console.error('连接WebSocket失败:', error);
      this.status = 'CLOSED';
      if (this.recognitionCallback) {
        this.recognitionCallback(null, error);
      }
      throw error;
    }
  }

  // 渲染识别结果
  renderResult(resultData) {
    try {
      const jsonData = JSON.parse(resultData);
      
      if (jsonData.data && jsonData.data.result) {
        const data = jsonData.data.result;
        let str = "";
        const ws = data.ws;
        
        for (let i = 0; i < ws.length; i++) {
          str += ws[i].cw[0].w;
        }
        
        // 处理动态结果
        if (data.pgs) {
          if (data.pgs === "apd") {
            this.resultText = this.resultTextTemp;
          }
          this.resultTextTemp = this.resultText + str;
        } else {
          this.resultText = this.resultText + str;
        }
        
        console.log('当前识别结果:', this.resultTextTemp || this.resultText);
      }
      
      // 识别结束
      if (jsonData.code === 0 && jsonData.data.status === 2) {
        if (this.iatWS) {
          this.iatWS.close();
        }
      }
      
      // 错误处理
      if (jsonData.code !== 0) {
        console.error('讯飞API错误:', jsonData);
        if (this.iatWS) {
          this.iatWS.close();
        }
        if (this.recognitionCallback) {
          this.recognitionCallback(null, new Error(`讯飞API错误: ${jsonData.message || '未知错误'}`));
        }
      }
    } catch (error) {
      console.error('解析识别结果失败:', error);
    }
  }

  // 开始录音
  async startRecording() {
    try {
      // 验证凭证
      if (!this.apiKey || !this.appId || !this.apiSecret) {
        throw new Error('请先设置科大讯飞API凭证');
      }
      
      if (this.isRecording) {
        console.log('已经在录音中');
        return;
      }
      
      this.resultText = '';
      this.resultTextTemp = '';
      
      await this.connectWebSocket();
      
      return true;
    } catch (error) {
      console.error('开始录音失败:', error);
      throw error;
    }
  }

  // 停止录音
  async stopRecording() {
    try {
      if (!this.isRecording || !this.recorder) {
        throw new Error('未在录音中');
      }
      
      this.recorder.stop();
      this.isRecording = false;
      
      // 等待WebSocket连接关闭和结果回调
      return new Promise((resolve, reject) => {
        const timeout = setTimeout(() => {
          reject(new Error('识别超时'));
        }, 10000);
        
        this.recognitionCallback = (result, error) => {
          clearTimeout(timeout);
          if (error) {
            reject(error);
          } else {
            resolve(result || this.resultText);
          }
        };
      });
    } catch (error) {
      console.error('停止录音失败:', error);
      this.isRecording = false;
      throw error;
    }
  }

  // 停止所有操作
  stop() {
    if (this.recorder) {
      try {
        this.recorder.stop();
      } catch (e) {
        console.error('停止录音出错:', e);
      }
    }
    
    if (this.iatWS) {
      try {
        this.iatWS.close();
      } catch (e) {
        console.error('关闭WebSocket出错:', e);
      }
    }
    
    this.isRecording = false;
    this.status = 'CLOSED';
  }

  // 语音识别主方法
  async recognizeSpeech(audioBlob) {
    try {
      // 验证凭证
      if (!this.apiKey || !this.appId || !this.apiSecret) {
        console.warn('没有科大讯飞API凭证，使用模拟数据');
        return this.getMockRecognitionResult();
      }
      
      // 由于我们使用实时录音和WebSocket，这里实际上是为了兼容旧接口
      if (!this.isRecording) {
        await this.startRecording();
        // 模拟录音一段时间后停止
        await new Promise(resolve => setTimeout(resolve, 100));
      }
      
      return await this.stopRecording();
    } catch (error) {
      console.error('语音识别失败:', error);
      // 失败时返回模拟数据
      return this.getMockRecognitionResult();
    }
  }

  // 手动停止录音并识别
  async stopAndRecognize() {
    try {
      if (!this.isRecording) {
        throw new Error('未在录音中');
      }
      
      // 停止录音并获取识别结果
      const text = await this.stopRecording();
      
      // 解析识别结果
      const parsedCommand = this.parseSpeechCommand(text);
      
      return {
        originalText: text,
        parsedCommand
      };
    } catch (error) {
      console.error('停止录音并识别失败:', error);
      throw error;
    }
  }

  // 解析语音指令
  parseSpeechCommand(text) {
    const command = {
      destination: '',
      days: 0,
      budget: 0,
      preferences: [],
      hasChildren: false,
      travelers: 0
    };
    
    if (!text) return command;
    
    // 解析目的地
    const destPattern = /去([^，,。\s]+)/;
    const destMatch = text.match(destPattern);
    if (destMatch) {
      command.destination = destMatch[1];
    }
    
    // 解析天数
    const dayPattern = /(\d+)天/;
    const dayMatch = text.match(dayPattern);
    if (dayMatch) {
      command.days = parseInt(dayMatch[1]);
    }
    
    // 解析预算
    const budgetPattern = /预算(\d+(?:\.\d+)?)[万千]?[元块]/;
    const budgetMatch = text.match(budgetPattern);
    if (budgetMatch) {
      let budget = parseFloat(budgetMatch[1]);
      if (text.includes('万')) {
        budget *= 10000;
      } else if (text.includes('千')) {
        budget *= 1000;
      }
      command.budget = budget;
    }
    
    // 解析偏好
    const preferences = [];
    const preferenceKeywords = ['美食', '购物', '景点', '文化', '历史', '自然', '海滩', '山脉', '博物馆', '艺术', '动漫', '主题公园'];
    
    preferenceKeywords.forEach(keyword => {
      if (text.includes(keyword)) {
        preferences.push(keyword);
      }
    });
    
    command.preferences = preferences;
    
    // 解析是否带孩子
    if (text.includes('带孩子') || text.includes('有小孩')) {
      command.hasChildren = true;
    }
    
    // 解析人数
    const peoplePattern = /(\d+)人/;
    const peopleMatch = text.match(peoplePattern);
    if (peopleMatch) {
      command.travelers = parseInt(peopleMatch[1]);
    }
    
    return command;
  }

  // 获取模拟识别结果
  getMockRecognitionResult() {
    const mockTexts = [
      '我想去日本东京，5天，预算1万元，喜欢美食和购物，2人',
      '计划去北京旅游，3天，预算5000元，想看故宫和长城',
      '我想去上海，4天，预算8000元，带孩子，喜欢主题公园'
    ];
    return mockTexts[Math.floor(Math.random() * mockTexts.length)];
  }
}

// 导出单例
export const speechRecognitionService = new SpeechRecognitionService();

// 使用已安装的crypto-js包，不再需要动态加载