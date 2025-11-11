<template>
  <div class="speech-test-container">
    <h1>语音识别功能测试</h1>
    <div class="test-info">
      <p>此页面用于测试科大讯飞语音识别功能，无需登录即可使用。</p>
    </div>
    
    <div class="test-controls">
      <button 
        @click="startTest" 
        :disabled="isRecording" 
        class="btn btn-success"
      >
        {{ isRecording ? '录音中...' : '开始录音' }}
      </button>
      <button 
        @click="stopTest" 
        :disabled="!isRecording" 
        class="btn btn-danger"
      >
        停止录音并识别
      </button>
      <button @click="clearResults" class="btn btn-secondary">
        清空结果
      </button>
    </div>
    
    <div class="results-section">
      <h3>识别结果</h3>
      <div class="result-box">
        <div v-if="recognitionResult" class="result-content">
          <p><strong>原始文本:</strong> {{ recognitionResult.originalText }}</p>
          <h4>解析结果:</h4>
          <div class="parsed-result">
            <div v-if="recognitionResult.parsedCommand.destination">
              <strong>目的地:</strong> {{ recognitionResult.parsedCommand.destination }}
            </div>
            <div v-if="recognitionResult.parsedCommand.days">
              <strong>天数:</strong> {{ recognitionResult.parsedCommand.days }} 天
            </div>
            <div v-if="recognitionResult.parsedCommand.budget">
              <strong>预算:</strong> ¥{{ recognitionResult.parsedCommand.budget }}
            </div>
            <div v-if="recognitionResult.parsedCommand.preferences">
              <strong>偏好:</strong> {{ recognitionResult.parsedCommand.preferences.join(', ') }}
            </div>
            <div v-if="recognitionResult.parsedCommand.hasChildren">
              <strong>带孩子:</strong> 是
            </div>
            <div v-if="recognitionResult.parsedCommand.travelers">
              <strong>人数:</strong> {{ recognitionResult.parsedCommand.travelers }} 人
            </div>
            <div v-if="Object.keys(recognitionResult.parsedCommand).length === 0">
              无法解析有效指令，请尝试更清晰的表述
            </div>
          </div>
        </div>
        <div v-else class="no-result">
          请点击"开始录音"按钮进行测试
        </div>
      </div>
      
      <div v-if="error" class="error-message">
        <strong>错误:</strong> {{ error }}
      </div>
    </div>
    
    <div class="logs-section">
      <h3>操作日志</h3>
      <div class="logs-box">
        <div v-for="(log, index) in logs" :key="index" class="log-item">
          {{ log }}
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { speechRecognitionService } from '../service/speechService';

export default {
  name: 'SpeechTestView',
  data() {
    return {
      isRecording: false,
      recognitionResult: null,
      error: null,
      logs: []
    };
  },
  mounted() {
    this.addLog('语音识别测试组件已加载');
    // 自动从环境变量加载配置到服务
    speechRecognitionService.setCredentials({
      appId: '74ddc3d1',
      apiKey: 'ae4ffca4d9240c5f3b0eac91cdfdb9d2',
      apiSecret: 'Zjc3MDIwM2Q3ZDczMWU1YTUxODY0Mjlm'
    })
;
  },
  methods: {
    async startTest() {
      try {
        this.error = null;
        this.isRecording = true;
        this.addLog('开始录音，请说话...');
        
        // 配置录音参数
        speechRecognitionService.setRecordingOptions({
          duration: 10000, // 10秒录音
          audioSettings: {
            sampleRate: 16000,
            channels: 1,
            encoding: 'wav'
          }
        });
        
        // 开始录音
        await speechRecognitionService.startRecording();
      } catch (err) {
        this.isRecording = false;
        this.error = err.message || '录音启动失败';
        this.addLog('录音开始失败: ' + (err.message || '未知错误'));
      }
    },
    async stopTest() {
      if (!this.isRecording) return;
      
      try {
        this.addLog('停止录音并开始识别...');
        this.recognitionResult = await speechRecognitionService.stopAndRecognize();
        this.isRecording = false;
        this.addLog('语音识别完成');
      } catch (err) {
        this.isRecording = false;
        this.error = err.message || '识别失败';
        this.addLog('识别失败: ' + (err.message || '未知错误'));
      }
    },
    clearResults() {
      this.recognitionResult = null;
      this.error = null;
      this.logs = [];
      this.addLog('结果已清空');
    },
    addLog(message) {
      const timestamp = new Date().toLocaleTimeString();
      this.logs.unshift(`[${timestamp}] ${message}`);
      // 只保留最近50条日志
      if (this.logs.length > 50) {
        this.logs.pop();
      }
    }
  }
};
</script>

<style scoped>
.speech-test-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  font-family: Arial, sans-serif;
}

h1, h3, h4 {
  color: #333;
}

.test-info {
  background-color: #e3f2fd;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 20px;
  border-left: 4px solid #2196f3;
}

.settings-section {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.test-controls {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #007bff;
  color: white;
}

.btn-success {
  background-color: #28a745;
  color: white;
}

.btn-danger {
  background-color: #dc3545;
  color: white;
}

.btn-secondary {
  background-color: #6c757d;
  color: white;
}

.results-section, .logs-section {
  margin-bottom: 20px;
}

.result-box, .logs-box {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 15px;
  min-height: 100px;
  background-color: #fafafa;
}

.no-result {
  color: #666;
  font-style: italic;
}

.error-message {
  background-color: #f8d7da;
  color: #721c24;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
  border: 1px solid #f5c6cb;
}

.parsed-result {
  background-color: #e9ecef;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.parsed-result > div {
  margin-bottom: 5px;
}

.log-item {
  border-bottom: 1px solid #eee;
  padding: 5px 0;
  font-size: 14px;
  color: #666;
}

.log-item:last-child {
  border-bottom: none;
}
</style>