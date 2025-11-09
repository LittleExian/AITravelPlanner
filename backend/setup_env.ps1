# 设置Java环境变量
$env:JAVA_HOME='C:\Program Files\Java\jdk-17'

# 设置Maven环境变量
$env:MAVEN_HOME='C:\Users\29799\apache-maven-3.9.4\apache-maven-3.9.4'

# 更新Path环境变量
$env:Path=$env:JAVA_HOME+'\bin;'+$env:MAVEN_HOME+'\bin;'+$env:Path

# 验证安装
Write-Host "=== Java 版本信息 ==="
java -version
Write-Host "\n=== Maven 版本信息 ==="
& "$env:MAVEN_HOME\bin\mvn.cmd" -version

# 构建并运行Spring Boot应用
Write-Host "\n=== 构建并运行Spring Boot应用 ==="
& "$env:MAVEN_HOME\bin\mvn.cmd" clean package spring-boot:run