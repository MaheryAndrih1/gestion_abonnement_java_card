@echo off
REM Lancer les tests

echo ============================================
echo  Execution des tests
echo ============================================
echo.

if not exist bin (
    echo ERREUR: Le projet n'est pas compile
    echo Executez d'abord: compile.bat
    pause
    exit /b 1
)

echo Test 1: Gestionnaire d'abonnement
echo ============================================
java -cp bin integration.AbonnementManagerSimple
echo.
echo.

echo Test 2: Tests complets
echo ============================================
java -cp bin test.TestAbonnementComplet
echo.

pause
