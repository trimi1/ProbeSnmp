
---

# Probe SNMP
## <ins>Prérequis</ins>

Avant de commencer, assurez-vous d'avoir les éléments suivants installés sur votre système :

- [Java](https:/jdk.java.net/21/)       : version 21.0.2
- **(pas nécessaire, normalement préinstallé sur le projet)** [Gradle](https:/gradle.org/install/)  : version 8.3

Vous pouvez vérifier si Java et Gradle sont correctement installés en exécutant les commandes suivantes dans votre terminal :

```bash
java -version
gradle -v
```

## <ins>Instructions d'installation</ins>

### Étape 1 : Cloner le dépôt Git

Clonez le dépôt Git de la Probe SNMP en utilisant la commande suivante :

```bash
git clone https://github.com/SxYxuse/ProbeSnmp.git
```

### Étape 2 : Accéder au répertoire du projet

Accédez au répertoire du projet en utilisant la commande suivante :

```bash
cd ProbeSnmp
```

### Étape 3 : Compiler et exécuter le projet

Compilez et exécutez le projet en utilisant Gradle :

```bash
gradle run
```

## <ins>Lors de l'exécution</ins>

- Échange entre Probe SNMP et Monitor Daemon
  - 1. **Monitor Daemon (M.D.)** : Démarré et en écoute d'annonces multicast.
  - 2. **Probe SNMP** : Annonce en multicast **```PROBE SNMP <PORT>```** toutes les 90 secondes.
  - 3. **M.D.** : À la réception de l'annonce, envoie la configuration à la Probe SNMP (fréquence de vérification des services et liste des services à vérifier).
  - 4. **Probe SNMP** : À la réception de la configuration, commence à superviser les services et à capturer leurs états.
  - 5. **Probe SNMP** : Après checking de tous les services, envoie une notification multicast **```DATA SNMP <PORT>```** au M.D. si un changement d'état est détecté.
  - 6. **M.D.** : À la réception de cette notification, se connecte à la Probe SNMP et récupère les états des services.

- Protocole entre M.D. et Probe
  - 1. **Message ```STATUSOF <ID>```** : Envoyé par le M.D. à la Probe SNMP pour demander l'état d'un service après avoir reçu une notification **```DATA SNMP <PORT>```**.
  - 2. **Message ```STATUS```** : Envoyé par la Probe SNMP au M.D. en réponse au message **```STATUSOF <ID>```** pour indiquer l'état actuel d'un service.

## <ins>Fichier de configuration</ins>

Le fichier `configProbes.json` est utilisé pour configurer la Probe SNMP. Il contient les informations suivantes :

- `protocol` : Le protocole utilisé, dans ce cas "snmp".
- `multicastAddress` : L'adresse multicast utilisée par la Probe SNMP.
- `multicastPort` : Le port multicast utilisé par la Probe SNMP.
- `multicastInterface` : L'interface multicast utilisée par la Probe SNMP.
- `multicastDelay` : Le délai entre chaque annonce multicast en milisecondes.
- `unicastPort` : Le port unicast utilisé par la Probe SNMP.
- `aesKey` : La clé AES utilisée pour le chiffrement.

Voici un exemple de fichier `configProbes.json` :

```json
{
  "protocol": "snmp",
  "multicastAddress": "224.0.0.254",
  "multicastPort": 60202,
  "multicastInterface": "ethernet_32768",
  "multicastDelay": 90,
  "unicastPort": 60000,
  "aesKey": "EPCtxxGqG1HUvD1LWaAbjLp+iU57QoKtcwn/yX8yM0g="
}
```

Assurez-vous de remplacer les valeurs par celles appropriées à votre environnement avant de lancer la Probe SNMP. (notamment l'interface multicast)

---
