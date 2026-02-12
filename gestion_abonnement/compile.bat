@echo off
REM Script de compilation pour Windows

echo ============================================
echo  Compilation du projet Gestion Abonnement
echo ============================================
echo.

REM Créer le dossier bin s'il n'existe pas
if not exist bin mkdir bin

echo [1/2] Compilation des sources...
REM Note: On ne compile pas l'applet Java Card (necessite javacard.jar)
REM On compile uniquement la version simplifiee avec simulateur

REM Compilation par ordre de dependances (avec encoding UTF-8)
javac -encoding UTF-8 -d bin src\integration\APDUConstants.java
javac -encoding UTF-8 -d bin -cp bin src\simulator\SimpleCardSimulator.java
javac -encoding UTF-8 -d bin -cp bin src\host\CardCommunicationSimple.java
javac -encoding UTF-8 -d bin -cp bin src\integration\AbonnementManagerSimple.java
javac -encoding UTF-8 -d bin -cp bin src\gui\AbonnementGUISimple.java
javac -encoding UTF-8 -d bin -cp bin src\gui\AbonnementGUIEnhanced.java
javac -encoding UTF-8 -d bin -cp bin src\test\TestAbonnementComplet.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR: La compilation a echoue
    pause
    exit /b 1
)

echo [2/2] Compilation reussie!
echo.
echo Fichiers compiles dans le dossier 'bin'
echo.
echo ============================================
echo  Compilation terminee avec succes
echo ============================================
echo.
echo Pour executer le projet:
echo   - GUI:  run_gui.bat
echo   - Test: run_test.bat
echo.
pause
