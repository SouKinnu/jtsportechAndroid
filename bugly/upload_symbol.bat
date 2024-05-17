@echo off
chcp 65001 > nul

setlocal

set /p version=请输入版本号: 

set "command=java -jar buglyqq-upload-symbol.jar -appid e65e107514 -appkey e8541a8f-49bf-4b0f-af22-9a25d75aeeb4 -bundleid com.jtsportech.visport.android -version %version% -platform Android -inputMapping F:\AndoridProject\CompanyProjects\jtsportechAndroid\app\build\outputs\mapping\release\mapping.txt"

echo 执行命令：%command%
%command%

echo 执行完毕，请查看结果。

endlocal