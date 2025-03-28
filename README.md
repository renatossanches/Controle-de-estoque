# Controle-de-Estoque

# LEIAM TODAS AS INSTRUÇÕES ABAIXO ANTES DE BAIXAR!!!

# Instrodução: 
Aqui deixo um projeto bem simples de Controle de Estoque feito em: 
- Java; 
- Javafx/Scene Builder; 
- Spring Boot;
- Lombok;
- Firebase e algumas bibliotecas.
- O sistema é baseado apenas em controle de quantidade e itens, é desktop, ou seja, focado apenas para instalação dentro de um computador.

# Funções: 
- Login;
- Cadastro(é feito pelo proprio site do Firebase Authenticator por email e senha);
- Adicionar, Alterar e Remover itens;
- Adicionar, Alterar e remover quantidades;
- Exportar Logs para ações feitas pelo usuário logado;
- Visualizar tabela dos itens e em texto;
- E algumas funções a mais.


# Sobre o projeto: 
Se necessário mandar mensagem para o Email: renato.sanches2004@gmail.com la tentarei solucionar seu problema da melhor forma possivel.


# Estrutura do projeto

```
├── Estoque
    ├── .gitattributes
    ├── .gitignore
    ├── README.md
    ├── dependency-reduced-pom.xml
    ├── help.txt
    ├── jre_custom.rar
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    └── src
    │   ├── build.fxbuild
    │   └── main
    │       ├── java
    │           ├── build.fxbuild
    │           └── com
    │           │   └── Estoque
    │           │       ├── Application.java
    │           │       ├── EstoqueApp.java
    │           │       ├── Main.java
    │           │       ├── api
    │           │           ├── AlertMsg.java
    │           │           ├── IdAutoIncrement.java
    │           │           ├── LoadFonts.java
    │           │           └── LoadScreen.java
    │           │       ├── config
    │           │           └── FirebaseConfig.java
    │           │       ├── controllerFXML
    │           │           ├── AddScreenController.java
    │           │           ├── AlterScreenController.java
    │           │           ├── ItemControllerFXML.java
    │           │           ├── LoginController.java
    │           │           ├── RelatoryController.java
    │           │           ├── RelatoryControllerData.java
    │           │           ├── RemoveScreenController.java
    │           │           └── ViewTableController.java
    │           │       ├── entities
    │           │           ├── Item.java
    │           │           └── LogsDTO.java
    │           │       ├── exception
    │           │           └── ExceptionService.java
    │           │       ├── repositories
    │           │           ├── EstoqueRepository.java
    │           │           ├── LogsRepository.java
    │           │           └── TokenAuthentication.java
    │           │       └── service
    │           │           └── EstoqueService.java
    │       └── resources
    │           ├── META-INF
    │               └── additional-spring-configuration-metadata.json
    │           ├── application.properties
    │           ├── com
    │               └── Estoque
    │               │   └── gui
    │               │       ├── AddScreen.fxml
    │               │       ├── AlterScreen.fxml
    │               │       ├── ItemEstoque.fxml
    │               │       ├── Login.fxml
    │               │       ├── Relatory.fxml
    │               │       ├── RelatoryData.fxml
    │               │       ├── RemoveScreen.fxml
    │               │       └── ViewTable.fxml
    │           ├── contents
    │               ├── screen2.css
    │               └── style.css
    │           ├── fonts
    │               ├── RETROTECH.ttf
    │               ├── game_over.ttf
    │               ├── goldenAge.ttf
    │               ├── good_timing.otf
    │               ├── ka1.ttf
    │               └── unlearn.ttf
    │           └── images
    │               ├── alertError.png
    │               ├── alertSuccess.png
    │               ├── background.jpg
    │               ├── btnAdd.png
    │               ├── btnAlter.png
    │               ├── btnRemove.png
    │               ├── ico.png
    │               ├── login.png
    │               ├── relatory.png
    │               └── spreadsheet.png
└── README.md
```

# Passo a passo de como criar a aplicação como executável:
1. Leiam o arquivo Help, lá explico todas as configurações necessárias para que rode a aplicação.
2. Como transformar o projeto em Jar

<a href="https://www.youtube.com/watch?v=MA6Uqsb4iAk" target="_blank">
    <div style="position: relative; display: inline-block;">
        <img src="https://img.youtube.com/vi/MA6Uqsb4iAk/maxresdefault.jpg" alt="Assista ao vídeo tutorial" width="400" />
    </div>
</a>

Caso não queira assistir siga os passos:
- Abra o projeto no Eclipse
- Com o botão direto do mouse em cima do projeto clique em: Run As> Maven Clean; depois: Run As> Maven Install
- O arquivo jar ficará localizado no path do projeto em Target




3. Como transformar o Jar em Exe
- Baixe o programa Launch4j: https://sourceforge.net/projects/launch4j/
- Após crie uma pasta com o nome de sua escolha, estarei usando "app" como exemplo
- Coloque na pasta o jar criado (esta locaizado em target com a extensão .jar) ;
  Um icone(para o programa, se preferir, com a extensão .ico);
  e a pasta jre_customizada ela está localizada na pagina inicial do projeto
- Agora assista ao pequeno tutorial:


<a href="https://www.youtube.com/watch?v=rPJqmKzaLgw" target="_blank">
    <div style="position: relative; display: inline-block;">
        <img src="https://img.youtube.com/vi/rPJqmKzaLgw/maxresdefault.jpg" alt="Assista ao vídeo tutorial" width="400" />
    </div>
</a>

Caso não queira assistir siga os passos:
- Abra o Launch4j;
- em OutputFile: coloque a pasta para salvar o Exe gerado com o nome de sua escolha e a extensão .exe;
- em Jar: selecione o jar da sua pasta app;
- (Se decidir colocar um icone no sistema) em Icon: selecione o icone com a extensão .ico;
- Agora vá em JRE: JRE_paths> mude o nome para jre_custom;
- No simbolo em cima de engrenagem, clique e salve o arquivo de configuração, e apos clique no play para rodar e testar o projeto.

   
4.  Como criar um instalador
- Baixe o programa Inno setup: https://jrsoftware.org/isdl.php;
- No site desça ate > Downloads Sites > e clique em US e faça a instalação;

<a href="https://www.youtube.com/watch?v=qWyctVAoOBo" target="_blank">
    <div style="position: relative; display: inline-block;">
        <img src="https://img.youtube.com/vi/qWyctVAoOBo/maxresdefault.jpg" alt="Assista ao vídeo tutorial" width="400" />
    </div>
</a>

Caso não queira assistir siga os passos:
- Abra o Inno Setup;
- clique em: Create a new script file using the Script Wizard> Ok> Next;
- em Application name: coloque o nome para seu app;
- Application version: inicie na versão preferivel mas sempre aumente o numero para ter update em seu projeto
- Em Application publisher: é quem fez o projeto;
- em Application Website: é o site do projeto;
- Clique em next
- Application main executable file> selecione aquele arquivo .exe gerado no Launch4j (ele precisa estar na mesma pasta da jre)
- Clique em add folder> selecione a pasta jre_custom> e após> parameters> Destination subfolder> coloque o nome jre_custom e> Next
- em Application file type name: mude para o mesmo nome do app;
- em Applicatio file type extension: mude para .exe e clique em > Next> Next> Next;
- em Install mode: deixe para non administrative, para permitir que qualquer pessoa instale seu app e depois clique em Next> Next;
- Em Languages: marque a opção "Brasilian Portuguese" e aperte em Next;
- Em Custom compiler output folder: selecione a pasta para salvar o instalador;
- Em Compile output base file name: escreva o nome para o instalador;
- Em Custom Setup icon file: selecione o icon se preferir, para o instalador e clique em Next>Next> Finish;
- salve o arquivo de configuração .iss e compile para salvar o instalador, se quiser teste a instação;


# Conclusão:
Futuramente creio que voltarei a trabalhar nesse projeto para adicionar ao item patrimônios/serial, para melhor controle das empresas.
