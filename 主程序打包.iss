; 脚本由 Inno Setup 脚本向导 生成！
; 有关创建 Inno Setup 脚本文件的详细资料请查阅帮助文档！

#define MyAppName "停车场车牌识别1.0.0.7"
#define MyAppVersion "1.0.0.7"
#define MyAppPublisher "东陆高新实业有限公司"
#define MyAppURL "http://www.dongluhitec.com/"

[Setup]
; 注: AppId的值为单独标识该应用程序。
; 不要为其他安装程序使用相同的AppId值。
; (生成新的GUID，点击 工具|在IDE中生成GUID。)
AppId={{7E10C778-C063-4E3B-92F0-355ABD9D8EC6}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
DefaultDirName={pf}\停车场车牌识别
DefaultGroupName=停车场车牌识别
DisableProgramGroupPage=yes
OutputDir=D:\
OutputBaseFilename=东陆停车场车牌识别1.0.0.7
Compression=lzma
SolidCompression=yes

[Languages]
Name: "chinesesimp"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\服务器.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\服务器.ini"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\更新方式.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\更新概述.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\客户端.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\客户端.ini"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\配置说明.txt"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\升级.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\升级.ini"; DestDir: "{app}"; Flags: ignoreversion
Source: "D:\git\dongluCarpark\target\carpark-201603240510-V1.0.0.7\bin\*"; DestDir: "{app}\bin"; Flags: ignoreversion recursesubdirs createallsubdirs
; 注意: 不要在任何共享系统文件上使用“Flags: ignoreversion”

;开始菜单快捷方式： [Icons]Name: "{group}\停车场服务器"; Filename: "{app}\服务器.exe";WorkingDir: "{app}"
Name: "{group}\停车场客户端"; Filename: "{app}\客户端.exe";WorkingDir: "{app}" 
;桌面快捷方式： Name: "{userdesktop}\停车场服务器"; Filename: "{app}\服务器.exe"; WorkingDir: "{app}"
Name: "{userdesktop}\停车场客户端"; Filename: "{app}\客户端.exe"; WorkingDir: "{app}"  
;开始菜单卸载快捷方式： 
Name: "{group}\{cm:UninstallProgram,卸载}"; Filename: "{uninstallexe}" 

