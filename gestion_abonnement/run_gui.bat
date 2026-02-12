@echo off
REM Lancer l'interface graphique

echo ============================================
echo  Lancement de l'interface graphique
echo ============================================
echo.

if not exist bin (
    echo ERREUR: Le projet n'est pas compile
    echo Executez d'abord: compile.bat
    pause
    exit /b 1
)

echo Demarrage de l'application...
echo.
java -cp bin gui.AbonnementGUIEnhanced

pause
