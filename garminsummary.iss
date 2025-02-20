; -- garminsummary.iss --
;

#include "Library.iss"

#define MyAppName "garminsummary"
#define MyAppVersion GetVersionNumbersString('target\garminsummary.exe')
#define MyAppExeName "garminsummary.exe"
#define MyIconFile "src\main\resources\GarminLogo.ico"
#define MyJavaMinVersion = 22

[Setup]
; SetupLogging=yes
PrivilegesRequired=poweruser

AppName={#MyAppName}
AppVersion={#MyAppVersion}
AppPublisher=RSH Kwee
AppPublisherURL=https://github.com/RSHKwee/garminsummary/releases
AppContact=rsh.kwee@gmail.com
WizardStyle=modern
DisableWelcomePage=no
DefaultDirName={code:MyConst}\{#MyAppName}
DefaultGroupName={#MyAppName}
UninstallDisplayIcon={app}\{#MyAppExeName}
InfoBeforeFile=readme.md
OutputDir=target
OutputBaseFilename={#MyAppName}_v{#MyAppVersion}_setup
UninstallFilesDir={app}\uninst
; Tell Windows Explorer to reload the environment
ChangesEnvironment=yes
SetupIconFile={#MyIconFile}

[Registry]
Root: HKCU; Subkey: "Environment"; ValueType:string; ValueName: "JAVA_HOME"; \
    ValueData: "{app}\jre"; Flags: preservestringtype; Check: JreNotPresent

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; \
    GroupDescription: "{cm:AdditionalIcons}"; Flags: unchecked

[Files]
Source: ".\target\{#MyAppExeName}"; DestDir: "{app}"; Flags: ignoreversion
Source: "readme.md"; DestDir: "{app}"; Flags: ignoreversion
Source: "readme.txt"; DestDir: "{app}"; Flags: isreadme
Source: ".\help\en\garminsummary.chm"; DestDir: "{app}\help\en"; Flags: ignoreversion
Source: ".\help\nl\garminsummary.chm"; DestDir: "{app}\help\nl"; Flags: ignoreversion
DestDir: {app}\jre; Source: ..\jre\*;   Flags: recursesubdirs ; Check: JreNotPresent

[Icons]
Name: "{commonstartup}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{userdesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Code]
const
  MyJavaMinVersion = {#MyJavaMinVersion};

var
  FinishedInstall: Boolean;

function InitializeSetup(): Boolean;
begin
  Log('InitializeSetup called');
  G_JavaMinVersion := MyJavaMinVersion;
  Result := true;
  jreNotChecked := true;
  L_jreNotPresent := true;
end;

procedure InitializeWizard;
begin
  Log('InitializeWizard called');
end;

<event('InitializeWizard')>
procedure InitializeWizard2;
begin
  Log('InitializeWizard2 called');
end;

procedure DeinitializeSetup();
begin
  Log('DeinitializeSetup called');
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  Log('CurStepChanged(' + IntToStr(Ord(CurStep)) + ') called');
  if CurStep = ssPostInstall then
    FinishedInstall := True;
end;

procedure CurInstallProgressChanged(CurProgress, MaxProgress: Integer);
begin
  Log('CurInstallProgressChanged(' + IntToStr(CurProgress) + ', ' + IntToStr(MaxProgress) + ') called');
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  Log('NextButtonClick(' + IntToStr(CurPageID) + ') called');
  Result := True;
end;

function BackButtonClick(CurPageID: Integer): Boolean;
begin
  Log('BackButtonClick(' + IntToStr(CurPageID) + ') called');
  Result := True;
end;

function ShouldSkipPage(PageID: Integer): Boolean;
begin
  Log('ShouldSkipPage(' + IntToStr(PageID) + ') called');
  { Skip wpInfoBefore page; show all others }
  case PageID of
    wpInfoBefore:
      Result := True;
  else
    Result := False;
  end;  
end;

procedure CurPageChanged(CurPageID: Integer);
begin
  Log('CurPageChanged(' + IntToStr(CurPageID) + ') called');
end;

function PrepareToInstall(var NeedsRestart: Boolean): String;
begin
  Log('PrepareToInstall() called');
  Result := '';
end;

