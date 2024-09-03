outputPath = 'build'

// Path where the docToolchain will search for the input files.
// This path is appended to the docDir property specified in gradle.properties
// or in the command line, and therefore must be relative to it.

inputPath = 'src/asciidoc/doc/product'

inputFiles = [
    [file: "01-Anwenderhandbuch/Anwenderhandbuch.adoc", formats: ['html', 'pdf']],
    // [file: "src/asciidoc/doc/product/Anforderungsdefinition/Anforderungsdefinition.adoc", formats: ['html']],
    // [file: "src/asciidoc/doc/projectManagement/Projekthandbuch/Projekthandbuch.adoc", formats: ['html']],
    // [file: "src/asciidoc/doc/projectManagement/AllgemeineDokumente/Abkürzungsverzeichnis.adoc", formats: ['html']],
    // [file: "src/asciidoc/doc/projectManagement/AllgemeineDokumente/Glossar.adoc", formats: ['html']],
    // [file: "src/asciidoc/doc/projectManagement/AllgemeineDokumente/Übersetzungen.adoc", formats: ['html']],
]

//folders in which asciidoc will find images.
//these will be copied as resources to ./images
//folders are relative to inputPath
imageDirs = [
    "01-Anwenderhandbuch/images",
]

taskInputsDirs = []
taskInputsFiles = []

//*****************************************************************************************

//tag::jbakeConfig[]

//customization of the Jbake gradle plugin used by the generateSite task
jbake.with {
    // possibility to configure additional asciidoctorj plugins used by jbake
    plugins = []

    // possibiltiy to configure additional asciidoctor attributes passed to the jbake task
    asciidoctorAttributes = []
}
//end::jbakeConfig[]

//*****************************************************************************************

//Configuration for microsite: generateSite + previewSite

microsite = [:]

// these properties will be set as jBake properties
// microsite.foo will be site.foo in jBake and can be used as config.site_foo in a template
// see https://jbake.org/docs/2.6.4/#configuration for how to configure jBake
// other properties listed here might be used in the jBake templates and thus are not
// documented in the jBake docs but hopefully in the template docs.
microsite.with {
    /** start:microsite **/

    // is your microsite deployed with a context path?
    contextPath = '/'
    // configure a port on which your preview server will run
    previewPort = 8042
    // the folder of a site definition (theme) relative to the docDir+inputPath
    //siteFolder = '../site'

    /** end:microsite **/

    //project theme
    //site folder relative to the docs folder
    //see 'copyTheme' for more details
    siteFolder = '../site'

    // the title of the microsite, displayed in the upper left corner
    title = '##site-title##'
    // the next items configure some links in the footer
    //
    // contact eMail
    // example: mailto:bert@example.com
    footerMail = '##footer-email##'
    //
    // twitter account url
    footerTwitter = '##twitter-url##'
    //
    // Stackoverflow QA
    footerSO = '##Stackoverflow-url##'
    //
    // Github Repository
    footerGithub = '##Github-url##'
    //
    // Slack Channel
    footerSlack = '##Slack-url##'
    //
    // Footer Text
    // example: <small class="text-white">built with docToolchain and jBake <br /> theme: docsy</small>
    footerText = '<small class="text-white">built with <a href="https://doctoolchain.org">docToolchain</a> and <a href="https://jbake.org">jBake</a> <br /> theme: <a href="https://www.docsy.dev/">docsy</a></small>'
    //
    // site title if no other title is given
    title = 'docToolchain'
    //
    // the url to create an issue in github
    // Example: https://github.com/docToolchain/docToolchain/issues/new
    issueUrl = '##issue-url##'
    //
    // the base url for code files in github
    // Example: https://github.com/doctoolchain/doctoolchain/edit/master/src/docs
    branch = System.getenv("DTC_PROJECT_BRANCH")?:'-'
    gitRepoUrl = '##git-repo-url##'

    //
    // the location of the landing page
    landingPage = 'landingpage.gsp'
    // the menu of the microsite. A map of [code:'title'] entries to specify the order and title of the entries.
    // the codes are autogenerated from the folder names or :jbake-menu: attribute entries from the .adoc file headers
    // set a title to '-' in order to remove this menu entry.
    menu = [:]

/**
tag::additionalConverters[]

if you need support for additional markup converters, you can configure them here
you have three different types of script you can define:

- groovy: just groovy code as string
- groovyFile: path to a groovy script
- bash: a bash command. It will receive the name of the file to be converted as first argument

`groovy` and `groovyFile` will have access to the file and config object

`dtcw:rstToHtml.py` is an internal script to convert restructuredText.
Needs `python3` and `docutils` installed.

end::additionalConverters[]
**/
    additionalConverters = [
        //'.one': [command: 'println "test"+file.canonicalPath', type: 'groovy'],
        //'.two': [command: 'scripts/convert-md.groovy', type: 'groovyFile'],
        //'.rst': [command: 'dtcw:rstToHtml.py', type: 'bash'],
    ]

    // if you prefer another convention regarding the automatic generation
    // of jBake headers, you can configure a script to modify them here
    // the script has access to
    // - file: the current object
    // - sourceFolder: the copy of the docs-source on which the build operates
    //                 default `/microsite/tmp/site/doc`
    // - config: the config object (this file, but parsed)
    // - headers: already parsed headers to be modified
    /**
    customConvention = """
        System.out.println file.canonicalPath
        headers.title += " - from CustomConvention"
    """.stripIndent()
    **/

    // if you need to register custom Asciidoctor extensions, this is the right place
    // configure the name and path to your extension, relative to the root of your project
    // (relative to dtcw). For example: 'src/ruby/asciidoctor-lists.rb'.
    // this is the same as the `requires`-list of the asciidoctor gradle plugin

    // rubyExtensions = []
}

//*****************************************************************************************


//Configuration for exportChangelog

exportChangelog = [:]

changelog.with {

    // Directory of which the exportChangelog task will export the changelog.
    // It should be relative to the docDir directory provided in the
    // gradle.properties file.
    dir = 'src/docs'

    // Command used to fetch the list of changes.
    // It should be a single command taking a directory as a parameter.
    // You cannot use multiple commands with pipe between.
    // This command will be executed in the directory specified by changelogDir
    // it the environment inherited from the parent process.
    // This command should produce asciidoc text directly. The exportChangelog
    // task does not do any post-processing
    // of the output of that command.
    //
    // See also https://git-scm.com/docs/pretty-formats
    cmd = 'git log --pretty=format:%x7c%x20%ad%x20%n%x7c%x20%an%x20%n%x7c%x20%s%x20%n --date=short'

}

//*****************************************************************************************

//tag::confluenceConfig[]
//Configureation for publishToConfluence

confluence = [:]

// 'input' is an array of files to upload to Confluence with the ability
//          to configure a different parent page for each file.
//
// Attributes
// - 'file': absolute or relative path to the asciidoc generated html file to be exported
// - 'url': absolute URL to an asciidoc generated html file to be exported
// - 'ancestorName' (optional): the name of the parent page in Confluence as string;
//                             this attribute has priority over ancestorId, but if page with given name doesn't exist,
//                             ancestorId will be used as a fallback
// - 'ancestorId' (optional): the id of the parent page in Confluence as string; leave this empty
//                            if a new parent shall be created in the space
// - 'preambleTitle' (optional): the title of the page containing the preamble (everything
//                            before the first second level heading). Default is 'arc42'
//
// The following four keys can also be used in the global section below
// - 'spaceKey' (optional): page specific variable for the key of the confluence space to write to
// - 'createSubpages' (optional): page specific variable to determine whether ".sect2" sections shall be split from the current page into subpages
// - 'pagePrefix' (optional): page specific variable, the pagePrefix will be a prefix for the page title and it's sub-pages
//                            use this if you only have access to one confluence space but need to store several
//                            pages with the same title - a different pagePrefix will make them unique
// - 'pageSuffix' (optional): same usage as prefix but appended to the title and it's subpages
// only 'file' or 'url' is allowed. If both are given, 'url' is ignored
confluence.with {

    input = [
            [file: "01-Anwenderhandbuch/Anwenderhandbuch.html"],
            // [file: "build/html5/src/asciidoc/doc/product/Anforderungsdefinition/Anforderungsdefinition.html"],
            // [file: "build/html5/src/asciidoc/doc/projectManagement/Projekthandbuch/Projekthandbuch.html"],
            // [file: "build/html5/src/asciidoc/doc/projectManagement/AllgemeineDokumente/Abkürzungsverzeichnis.html"],
            // [file: "build/html5/src/asciidoc/doc/projectManagement/AllgemeineDokumente/Glossar.html"],
            // [file: "build/html5/src/asciidoc/doc/projectManagement/AllgemeineDokumente/Übersetzungen.html"],
    ]

    // endpoint of the confluenceAPI (REST) to be used
    // verify that you got the correct endpoint by browsing to
    // https://[yourServer]/[context]/rest/api/user/current
    // you should get a valid json which describes your current user
    // a working example is https://arc42-template.atlassian.net/wiki/rest/api/user/current
    api = 'XXXXXXXXXXXXXXXX'

    //    Additionally, spaceKey, createSubpages, pagePrefix and pageSuffix can be globally defined here. The assignment in the input array has precedence

    // the key of the confluence space to write to
    // spaceKey = 'FIS'
    spaceKey = 'XXXXXXXXXXXXXXXX'

    ancestorId = '3049708428'

    // variable to determine whether the whole document should be uploaded as just one page or split into separate
    // pages per chapter
    // Deprecated configuration, migrate as follows:
    // allInOnePage = true -> subpagesForSections = 0
    // allInOnePage = false && createSubpages = false -> subpagesForSections = 1
    // allInOnePage = false && createSubpages = true -> subpagesForSections = 2
    subpagesForSections = 0

    // the pagePrefix will be a prefix for each page title
    // use this if you only have access to one confluence space but need to store several
    // pages with the same title - a different pagePrefix will make them unique
    pagePrefix = 'XXXXXXXXXXXXXXXX '

    pageSuffix = ''

    /*
    WARNING: It is strongly recommended to store credentials securely instead of commiting plain text values to your git repository!!!

    Tool expects credentials that belong to an account which has the right permissions to to create and edit confluence pages in the given space.
    Credentials can be used in a form of:
     - passed parameters when calling script (-PconfluenceUser=myUsername -PconfluencePass=myPassword) which can be fetched as a secrets on CI/CD or
     - gradle variables set through gradle properties (uses the 'confluenceUser' and 'confluencePass' keys)
    Often, same credentials are used for Jira & Confluence, in which case it is recommended to pass CLI parameters for both entities as
    -Pusername=myUser -Ppassword=myPassword
    */
    bearerToken = "XXXXXXXXXXXXXXXX"

    //optional API-token to be added in case the credentials are needed for user and password exchange.
    //apikey = "[API-token]"

    // HTML Content that will be included with every page published
    // directly after the TOC. If left empty no additional content will be
    // added
    extraPageContent = '<ac:structured-macro ac:name="warning"><ac:parameter ac:name="title" /><ac:rich-text-body>Diese Confluence-Seite wird aus AsciiDoc generiert! Anpassungen an der Dokumentation sollte IMMER über das Git-Repository https://devstack.vwgroup.com/bitbucket/projects/FISNX/repos/gaf-nx-doc/browse durchgeführt werden.</ac:rich-text-body></ac:structured-macro>'
    // extraPageContent = ''

    // enable or disable attachment uploads for local file references
    enableAttachments = false

    // default attachmentPrefix = attachment - All files to attach will require to be linked inside the document.
    // attachmentPrefix = "attachment"


    // Optional proxy configuration, only used to access Confluence
    // schema supports http and https
    // proxy = [host: 'my.proxy.com', port: 1234, schema: 'http']

    // Optional: specify which Confluence OpenAPI Macro should be used to render OpenAPI definitions
    // possible values: ["confluence-open-api", "open-api", "swagger-open-api", true]. true is the same as "confluence-open-api" for backward compatibility
    // useOpenapiMacro = "confluence-open-api"
}
//end::confluenceConfig[]


//tag::collectIncludesConfig[]
collectIncludes = [:]

collectIncludes.with {

//    fileFilter = "adoc" // define which files are considered. default: "ad|adoc|asciidoc"

//    minPrefixLength = "3" // define what minimum length the prefix. default: "3"

//    maxPrefixLength = "3" // define what maximum length the prefix. default: ""

//    separatorChar = "_" // define the allowed separators after prefix. default: "-_"

//    cleanOutputFolder = true // should the output folder be emptied before generation? default: false
}
//end::collectIncludesConfig[]