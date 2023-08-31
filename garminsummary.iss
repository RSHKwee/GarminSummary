; -- ing2ofx.iss --
;
#define MyAppName "garminsummary"
#define MyAppVersion GetVersionNumbersString('target\garminsummary.exe')
#define MyAppExeName "garminsummary.exe"
#define MyIconFile "src\main\resources\GarminLogo.ico"

[Setup]
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
OutputBaseFilename={#MyAppName}_v{#MyAppVersion}_jre_setup
UninstallFilesDir={app}\uninst
; Tell Windows Explorer to reload the environment
ChangesEnvironment=yes
SetupIconFile={#MyIconFile}
; SetupLogging=yes
; PrivilegesRequired=poweruser

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
var
  jreNotChecked : Boolean;
  FinishedInstall: Boolean;
  L_jreNotPresent: boolean;

function InitializeSetup(): Boolean;
begin
  Log('InitializeSetup called');
  Result := true;
  jreNotChecked := true;
  L_jreNotPresent := true;
end;

procedure InitializeWizard;
begin
  Log('InitializeWizard called');
end;

function GetJavaMajorVersion(): integer;
var
  TempFile: string;
  ResultCode: Integer;
  S: AnsiString;
  P: Integer;
begin
  Result := 0;

  { execute java -version and redirect output to a temp file }
  TempFile := ExpandConstant('{tmp}\javaversion.txt');
  if (not ExecAsOriginalUser(ExpandConstant('{cmd}'), '/c java -version 2> "' + TempFile + '"', '',SW_HIDE, ewWaitUntilTerminated, ResultCode)) 
    or (ResultCode <> 0) then
  begin
    Log('Failed to execute java -version');
    exit;
  end;

  { read file into variable S }
  LoadStringFromFile(TempFile, S)
  DeleteFile(TempFile);
  Log(Format('java -version output: ' + #13#10 + '%s', [S]));

  { extract version (between quotes) }
  P := Pos('"', S);
  Delete(S, 1, P);
  P := Pos('"', S);
  SetLength(S, P - 1);
  Log(Format('Extracted version: %s', [S]));

  { extract major }
  if Copy(S, 1, 2) = '1.' then
  begin
    Delete(S, 1, 2)
  end;
  P := Pos('.', S);
  SetLength(S, P - 1);
  Log(Format('Major version: %s', [S]));

  Result := StrToIntDef(S, 0);
end;

function JreNotPresent: Boolean;
begin
  if jreNotChecked then
  begin
    if (GetJavaMajorVersion() > 16) then
    begin
      L_jreNotPresent := false;
      Log('Java jre is present.');
    end else begin
      L_jreNotPresent := true;
      Log('Java jre is not present, define JAVA_HOME.');
    end;
    jreNotChecked := false;
  end;
  Result := L_jreNotPresent;
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

function MyConst(Param: String): String;
begin
  Log('MyConst(''' + Param + ''') called');
  Result := ExpandConstant('{autopf}');
end;

