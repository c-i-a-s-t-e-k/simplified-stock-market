## Setup and start (Windows PowerShell)

To run the project you will need Docker Desktop and an available internet connection to download all required Java dependencies and the PostgreSQL database Docker image.

Before running any script for the first time, allow PowerShell to execute local scripts (run once as Administrator):
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

To build and run the app:
```powershell
.\build_and_run.ps1 8081 8082 8083 # number of ports — 1 port = 1 app instance
```

If already built, you can get a fresh environment with the following commands. Note: these commands work only on containers created with the build script. If you want to set up a different number of instances, you need to remove the current environment first and then build again.
```powershell
.\stop.ps1
.\start.ps1
```

To remove containers and clean up:
```powershell
.\remove.ps1
```
---

due to lack of knowledge of poverShell all scripts are writed in Claude and tested via githubActions
