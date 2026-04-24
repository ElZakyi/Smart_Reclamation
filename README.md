# 🚀 Setup Instructions
# ✅ Prérequis

Avant de lancer l’application, installer les outils suivants :

- Git
- Java 17
- Maven
- Node.js 18+
- npm
- Python 3.10+
- PostgreSQL
- pip

## Vérification des installations

```bash
git --version
java -version
mvn -version
node -v
npm -v
python --version
pip --version
psql --version
## 1. cloner le projet 
```bash
git clone <URL_DU_REPO>
cd pfe_app
## 2. Créer la base de données PostgreSQL
psql -U postgres
CREATE DATABASE cih_db;
\q
## 3. Lancer le Backend (Spring Boot)
cd cih-backend
mvn clean install
mvn spring-boot:run
## 4. Lancer les services IA
🔹 Service Classification
cd ai-service
pip install -r requirements.txt
uvicorn ai_Classifier:app --reload --port 8000
🔹 Service Routing

(Ouvrir un nouveau terminal)

cd ai-service
uvicorn ai_RoutingSuggestion:app --reload --port 8001
## 5. Lancer le Frontend (Next.js)

(Ouvrir un nouveau terminal)

cd cih-frontend
npm install
npm run dev
🌐 Accès à l'application
Frontend : http://localhost:3000
Backend : http://localhost:8080
IA Classifier : http://localhost:8000
IA Routing : http://localhost:8001
