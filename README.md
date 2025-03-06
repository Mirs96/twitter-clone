# Twitter Clone

Un'applicazione **full-stack** che replica le funzionalità di base di Twitter, incluse l'autenticazione, la creazione di tweet e la gestione degli hashtag.

## 📁 Struttura del Progetto

**Frontend (Angular):**  
- **Autenticazione (Login/Registrazione)**  
- **Home** (vuota, in fase di sviluppo)  
- **Navbar laterale** (non ancora funzionale)

**Backend (Spring Boot):**  
- **Login/Registrazione**  
- **Creazione dei tweet** (gestione automatica degli hashtag)  
- **Associazione degli hashtag ai tweet**  
- **API RESTful con Spring JPA (Hibernate) e MVC**

## 🛠 Tecnologie Utilizzate

**Frontend (Angular):**  
- **Angular**  
- **TypeScript**  
- **CSS**

**Backend (Spring Boot):**  
- **Spring Boot**  
- **Spring Security**  
- **Spring JPA (Hibernate)**  
- **PostgreSQL**

## **Funzionalità**

### 📌 Endpoint API principali

**Autenticazione**  
- **Login:** `POST /api/auth/login`  
- **Registrazione:** `POST /api/auth/register`

**Gestione dei Tweet**  
- **Creazione di un Tweet:** `POST /api/tweet`  
  (Nota: il backend gestisce automaticamente gli hashtag nei tweet)

### 📜 TODO (Prossimi Passi)

- ✅ **Backend:** Gestione dinamica degli hashtag  
- ❌ **Frontend:** Visualizzazione dei tweet e funzionalità della navbar  
- ❌ Aggiungere like, retweet e profili utente
