@echo off
:: Kiểm tra quyền quản trị
net session >nul 2>&1
if %errorLevel% == 0 (
    :: Quyền quản trị đã có, chạy script Python
    py chongddos/chongddos.py
) else (
    :: Yêu cầu quyền quản trị
    echo Yêu cầu quyền quản trị...
    powershell -Command "Start-Process cmd -ArgumentList '/c py chongddos/chongddos.py' -Verb RunAs"
)
