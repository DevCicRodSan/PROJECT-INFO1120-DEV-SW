# PROJECT-INFO1120-DEV-SW AKA Pepper Music Player

Gerador de sequências musicais a partir de texto INF01120 

## Requisitos

| Ferramenta | Versão | Como conferir |
|---|---|---|
| JDK (Java Development Kit) | 21 (a mesma do `pom.xml`) | `java -version` |
| Apache Maven | 3.8 ou superior | `mvn -v` |
| Saída de áudio | alto-falante ou fone funcionando | – |
| Internet | só no primeiro build (baixa as dependências) | – |

O Swing (interface gráfica) já vem no JDK; não é preciso instalar mais nada.

### Instalando o Maven no Windows

1. Baixe o ZIP "Binary" em <https://maven.apache.org/download.cgi> e extraia, por exemplo, em `C:\Tools\apache-maven-3.9.x`.
2. Crie a variável de ambiente `JAVA_HOME` apontando para a pasta do JDK.
3. Adicione `C:\Tools\apache-maven-3.9.x\bin` ao `Path`.
4. Abra um **novo** terminal e confira com `mvn -v`.

## Como rodar

Na pasta do projeto (onde está o `pom.xml`):

```bash
mvn clean package
java -jar target/pepper_music_player-1.0-SNAPSHOT.jar
```

- `mvn clean package` apaga o `target/`, compila e gera um JAR único (*fat JAR*, via `maven-shade-plugin`) que já inclui o JFugue.
- Você precisa repetir o `mvn clean package` sempre que alterar o código. Para só checar se compila: `mvn compile`.
- O JAR gerado roda em qualquer máquina com Java instalado, sem Maven.

### Pelo VSCode

1. Instale o **Extension Pack for Java** (inclui *Maven for Java*).
2. Abra a pasta do projeto (a que contém o `pom.xml`).
3. Execute a classe `com.example.Main` pelo botão **Run** acima do método `main`, ou use o painel *Maven* → `Lifecycle` → `package`.

## Dependências (resolvidas pelo Maven)

- **JFugue 5.0.9** (`jfugue:jfugue`). Não está no Maven Central; está publicada no repositório Maven do **SING Group**, por isso o `pom.xml` declara o repositório `https://maven.sing-group.org/repository/maven/`.