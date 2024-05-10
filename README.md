## Spring HTTPS & Websocket

## HTTPS 配置

### 1. 生成证书

```shell
keytool -genkeypair -v -alias spring_https -keypass infilos@ssl -keyalg RSA -keysize 2048 -validity 3650 -dname "cn=测试数科,ou=杭州测试数字科技有限公司,o=技术部,l=杭州,st=浙江,c=cn" -storepass infilos@ssl -keystore spring_https.jks
```

- alias 别名
- keypass 指定生成密钥的密码
- keyalg 指定密钥使用的加密算法（如 RSA）
- keysize 密钥大小
- validity 过期时间，单位天
- keystore 指定存储密钥的密钥库的生成路径、名称
- storepass 指定访问密钥库的密码

得到名为 `spring_https.keystore` 文件，分别方位 server 和 client 的 `resource/ssl/` 目录中。

> 需要确保使用 keytool 的命令行环境的 Java JDK 与使用该证书的 Java JDK 版本一致，否则会出现 `Invalid keystore format` 异常。

### 2. 生成 Nginx 配置

```shell
keytool -export -alias spring_https -file wss_server.der -keystore spring_https.jks
openssl x509 -inform der -in wss_server.der -out wss_server.pem

keytool -importkeystore -srckeystore spring_https.jks -destkeystore keystore.p12 -deststoretype PKCS12
openssl pkcs12 -in keystore.p12  -nodes -nocerts -out wss_server.key
```

得到名为 `wss_server.pem` 和 `wss_server.key` 的文件，用于配置 Nginx。
