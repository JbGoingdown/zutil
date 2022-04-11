# zutil

## 1.0.0
### common
|工具类 			|说明 			|
|-----------------|-----------------|
|EscapeUtils  	  |转义或反转义		|
|HtmlFilter   	  |请求特殊字符过滤	   |
|IdUtils 	  	  |ID生成工具类		 |
|IdWorker 	  	  |雪花算法 		 |
|AjaxResult   	  |请求响应结果 		|
|SqlUtil 	  	  |SQL特殊字符校验 	|
|DateUtils 	  	  |日期工具类 		 |
|HttpUtils 	  	  |HTTP工具类		  |
|ObjectUtils  	  |对象处理工具类 	    |
|ReflectUtils 	  |反射工具类 		 |
|StringUtils  	  |字符串处理工具类    |
|WebUtils	  	  |网络地址处理类      |

### crypto
|工具类 			|说明 			|
|-----------------|-----------------|
|DesUtils   	  |Des 加解密 		  |
|Des3Utils 	  	  |3Des 加解密		  |
|AesUtils	  	  |AES 加解密        |
|PbeUtils	  	  |PBE 加解密 		  |
|IdeaUtils	 	  |IDEA 加解密       |
|DHUtils	  	  |DH 加解密         |
|RsaUtils 	  	  |RSA 加解密        |
|ElGamalUtils 	  |ElGamal 加解密    |
|RsaSignUtils  	  |RSA 数字签名      |
|DsaSignUtils 	  |DSA 数据签名      |
|EcdsaSignUtils	  |ECDSA 数字签名    |

### log
- 启动类  
  `cn.zm1001.util.log.LogbackConfigLoader#init`
- 配置文件  
  `resources/log.properties`
```
#项目唯一标识，默认：not_provide_appUK
log_app_uk=
#日志级别，默认：info
log_level=info
#日志环境(test/product)
log_env=test
#日志存放文件夹，默认：/var/log
#log_path=
#日志文件名，默认：logback.log
#log_file_name=
#日志存放天数，默认：30天
#log_max_history=
#单日志文件大小，默认：100MB
#log_max_file_size=
```

### mail
|工具类 			|说明 			|
|-----------------|-----------------|
|MailUtils   	  |邮件工具类         |

### POI
|工具类 			|说明 			|
|-----------------|-----------------|
|ExcelUtils   	  |Excel导入导出     |

### WEB
|工具类 			|说明 			|
|-----------------|-----------------|
|ServletUtils     |Servlet工具类     |
|UrlUtils         |URL工具类         |
|SpringUtils      |Spring工具类      |
|MessageUtils     |i18n国际化工具类   |
|CaptchaGenerator |验证码生成器       |
|XssFilter		  |XSS过滤器         |
|RepeatableFilter |请求可重复读取      |
|DuplicateSubmitInterceptor |重复提交拦截器 |
|BaseController	  |Controller通用数据处理 |


