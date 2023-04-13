; -- garminsummary_jreDownload.iss --
;
#define MyAppName "garminsummary"
#define MyAppVersion GetVersionNumbersString('target\garminsummary.exe')
#define MyAppExeName "garminsummary.exe"
#define MyIconFile "src\main\resources\garminLogo.ico"

#define JreUrl =  "https://www.eclipse.org/downloads/download.php?file=/justj/jres/17/downloads/20230204_0657/org.eclipse.justj.openjdk.hotspot.jre.minimal.stripped-17.0.6-win32-x86_64.tar.gz"

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
OutputBaseFilename={#MyAppName}_v{#MyAppVersion}_jreDownload_setup
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
Source: ".\help\garminsummary.chm"; DestDir: "{app}"; Flags: ignoreversion

[Dirs]
Name: {app}\jre

[Icons]
Name: "{commonstartup}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"
Name: "{userdesktop}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}"; Tasks: desktopicon

[Code]
var
  jreNotChecked : Boolean;
  FinishedInstall: Boolean;
  L_jreNotPresent: boolean;
 
  DownloadPage: TDownloadWizardPage;

function InitializeSetup(): Boolean;
begin
  Log('InitializeSetup called');
  Result := true;
  jreNotChecked := true;
  L_jreNotPresent := true;
end;

function OnDownloadProgress(const Url, FileName: String; const Progress, ProgressMax: Int64): Boolean;
begin
  if Progress = ProgressMax then
    Log(Format('Successfully downloaded file to {tmp}: %s', [FileName]));
  Result := True;
end;

procedure ExtractTarGz(TarGzFile, DestDir: string);
var
  ResultCode: Integer;
begin
  Exec('C:\Windows\System32\tar.exe', '-xvzf "' + TarGzFile + '" -C "' + DestDir +'"', '', SW_HIDE,ewWaitUntilTerminated, ResultCode);
  Log('tar 1 ResultCode: ' + IntToStr(ResultCode));
  Log('');
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
  DownloadPage := CreateDownloadPage(SetupMessage(msgWizardPreparing), SetupMessage(msgPreparingDesc), @OnDownloadProgress);
  Log('InitializeWizard2 called');
end;

function NextButtonClick(CurPageID: Integer): Boolean;
begin
  if ((CurPageID = wpReady) AND JreNotPresent()) then
  begin
    DownloadPage.Clear;
    DownloadPage.Add(ExpandConstant('{#JreUrl}' + '&r=1'), 'jre.tar.gz', '');
    DownloadPage.Show;
    try
      try
        DownloadPage.Download; // This downloads the files to {tmp}
        Result := True;
      except
        if DownloadPage.AbortedByUser then
          Log('Aborted by user.')
        else
          SuppressibleMsgBox(AddPeriod(GetExceptionMessage), mbCriticalError, MB_OK, IDOK);
        Result := False;
      end;
    finally
      DownloadPage.Hide;
    end;
  end else begin
    Result := True;
  end;  
end;

procedure DeinitializeSetup();
begin
  Log('DeinitializeSetup called');
end;

procedure CurStepChanged(CurStep: TSetupStep);
begin
  Log('CurStepChanged(' + IntToStr(Ord(CurStep)) + ') called');
  if ((CurStep = ssPostInstall) and JreNotPresent()) then
  begin
    ExtractTarGz(ExpandConstant('{tmp}\jre.tar.gz'), ExpandConstant('{app}\jre'));
    FinishedInstall := True;
  end;
end;

procedure CurInstallProgressChanged(CurProgress, MaxProgress: Integer);
begin
  Log('CurInstallProgressChanged(' + IntToStr(CurProgress) + ', ' + IntToStr(MaxProgress) + ') called');
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

