**Francesco Forcellato**
# ChatTCP
Il progetto comprende i seguenti file:
	
	ClientMessaggi.java
	ClientG.java
	Login.java
	Messaggio.java
	ServerMessaggi.java
	SQLHelper.java
	Account.java
	Account.db

## Client
Il Client è formato da 4 classi:

- `ClientMessaggi.java`
- `ClientG`
- `Login.java`
- `Messaggio.java`

`ClientMessaggi.java` permette di eseguire tutte le funzionalità del client: ha infatti il compito di istanziare il _login_; una volta che l'utente si è registrato ha il compito di inizializzare il client effettivo che permette di inviare i messaggi tra un client e l'altro attraverso un'interfaccia grafica. Attraverso `Login.java`, _ClientMessaggi_ ottiene i dati dell'utente, che a seconda delle richieste (_sign in_ o _sign up_) deve inviare a `ServerMessaggi.java` i dati dell'utente, una volta che ha effettuato l'accesso, _ClientMessaggi_ crea la grafica del client, attraverso l'utilizzo di `ClientG.java` e ha il compito di settare tutte le conversazioni e gli utenti registrati nell'interfaccia grafica. _ClientG_ ha poi il compito di accogliere le richieste dell'utente, inviandole attraverso _ClientMessaggi_ al server. _ClientMessaggi_ ha al suo interno la classe `Ascoltatore` che ha il compito di ascoltare ed accogliere i messaggi che riceve dal server per poi visualizzarli su _ClientG_, l'interfaccia grafica.

`Messaggio.java` è la classe che definisce l'oggetto che viene passato tra client e server, contenente il messaggio tra gli utenti, definendo mittente, destinatario, messaggio e data di invio.




## Server
Il server è invece formato dai seguenti file:
- `ServerMessaggi.java`
- `SQLHelper.java`
- `Account.java`
- `Account.db`
- `Messaggio.java`

`ServerMessaggi.java` è il server effettivo, il suo compito è quello di ascoltare e attendere che un client si colleghi; una volta che un client si è collegato, lancia un Thread, la cui classe **Destinatario** è definita dentro `ServerMessaggi.java`, questo Thread ha il compito di accogliere ed eseguire tutte le richieste dell'utente. Il server ha poi un ArrayList di oggetti `Account.java`: l'oggetto _Account_ contiene tutte le informazioni del singolo utente registrato e il socket che definisce la comunicazione con quell'utente se online.

_Destinatario_ ha i seguenti compiti:
1) Far eseguire l'accesso ai client collegati, registrando nuovi utenti o facendo eseguire il login agli utenti già registrati.
2) Inviare all'utente che si è collegato tutti gli utenti con cui può chattare e tutte le conversazioni che ha effettuato precedentemente.
3) Inviare i messaggi dal mittente al destinatario.

Per poter registrare gli utenti _Destinatario_ si serve di un database per salvare i dati: `Account.db`. Per poter eseguire tutte le operazioni sul database si serve poi di `SQLHelper.java`, una volta che riceve username e password dal client, attraverso i metodi statici di _SQLHelper_, se l'utente non è già presente nel database allora lo registra e poi invia un messaggio _broadcast_ per poter aggiornare le liste di utenti con cui si può chattare a tutti i client, poi prende i suoi dati e li inserisce in un oggetto _Account_ che poi aggiunge all'ArrayList. Successivamente invia al client che si è appena collegato l'oggetto che contiene tutti gli utenti registrati nel database in modo tale che il client possa visualizzare tutti gli utenti con cui è possibile effettuare una conversazione, permettendone l'interazione. Successivamente invia al client tutti messaggi delle conversazioni associate all'utente che ha eseguito l'accesso.

Una volta finita questa operazione, può avere inizio la comunicazione e lo sbambio di messaggi:
ora _Destinatario_ rimane in attesa di oggetti `Messaggio.java` da parte del client a cui è collegato, una volta che ne riceve uno, salva il messaggio nel database, poi lo invia sia al destinatario che al mittente, in questo modo si può essere certi che il messaggio sia stato ricevuto dal server e inviato al destinatario.




## Database
Il database è composto di 2 tabelle:
1) `ACCOUNT`
2) `CONVERSAZIONI`

La prima tabella contiene tutti gli utenti che sono registrati: è composta di 2 colonne, `Username` e `Password`. La colonna `Username` contiene appunto il nome dell'account associato all'utente che è _chiave primaria_, la colonna `Password` contiene la password associata all'_username_. Grazie a questa tabella è possibile verificare se l'utente che sta tentando l'accesso sia già presente nel database e che la password inserita corrisponda a quella registrata.

La seconda tabella invece contiene tutti i messaggi che sono stati scambiati: è composta di 4 colonne.
1) `Mittente`: contiene una _chiave esterna_ associata alla tabella `ACCOUNT` a cui corrisponde l'`Username` del mittente.
2) `Destinatario`: contiene una _chiave esterna_ associata alla tabella `ACCOUNT` a cui corrisponde l'`Username` del destinatario.
3) `Messaggio`: contiene il messaggio che il mittente vuole inviare al destinatario.
4) `Data`: contiene la data in cui è stato spedito il messaggio, in modo tale che poi nella ricostruzione delle conversazioni, sia possibile ordinare i messaggi per data.
