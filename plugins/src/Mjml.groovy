package com.suryadigital.leo.plugins

import freemarker.template.Configuration
import freemarker.template.TemplateExceptionHandler
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Plugin
import org.gradle.api.Project
import javax.net.ssl.HttpsURLConnection

/**
 * Plugin that reads the Freemarker (https://freemarker.apache.org) template and generates HTML from the generated MJML.
 */
class RenderMjmlTemplate implements Plugin<Project> {
    @Override
    void apply(Project project) {
        def renderMjmlTemplateExt = project.extensions.create("renderMjmlTemplateConfig", RenderMjmlTemplateConfig)

        /**
         * Task that converts Freemarker templates to MJML, which in turn is converted to HTML.
         * It can be trigerred by running `./gradlew generateHtmlFromTemplate`
         */
        project.tasks.register("renderMjmlTemplate") {
            doLast {
                def rootDirectory = new File("${project.rootDir}")
                def cfg = new Configuration(Configuration.VERSION_2_3_29)
                cfg.setDefaultEncoding("UTF-8");
                cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                cfg.setDirectoryForTemplateLoading(rootDirectory);
                cfg.setLogTemplateExceptions(false);
                cfg.setWrapUncheckedExceptions(true);
                cfg.setFallbackOnNullLoopVariable(false);
                rootDirectory.traverse(maxDepth: renderMjmlTemplateExt.maxDepth) { templateFile ->
                    if (templateFile.path.contains("/build/")) {
                        return
                    }
                    if (templateFile.name == renderMjmlTemplateExt.inputFile) {
                        def relativePath = rootDirectory.relativePath(templateFile)
                        def template = cfg.getTemplate(relativePath)
                        Writer out = new StringWriter();
                        HashMap<String, String> root
                        template.process(root, out);
                        def generatedTemplate = out.toString()
                        if (renderMjmlTemplateExt.generateMjml) {
                            def newFile = new File("${templateFile.parent}/generated.mjml")
                            newFile.delete()
                            newFile.createNewFile()
                            newFile.text = generatedTemplate
                        }
                        def data = [
                                mjml: generatedTemplate,
                        ]
                        def object = JsonOutput.toJson(data)
                        def post = new URL("https://api.mjml.io/v1/render").openConnection() as HttpsURLConnection
                        post.setRequestMethod("POST")
                        post.setRequestProperty("Content-Type", "application/json")
                        post.setRequestProperty("Authorization", renderMjmlTemplateExt.authHeader)
                        post.setRequestProperty("Accept", "application/json")
                        post.setDoOutput(true)
                        post.getOutputStream().write(object.bytes)
                        try {
                            BufferedReader br = new BufferedReader(new InputStreamReader(post.getInputStream(), "utf-8"))
                            def jsonSlurp = new JsonSlurper()
                            def responseHtml = jsonSlurp.parseText(br.text).html
                            def newFile = new File("${templateFile.parent}/${renderMjmlTemplateExt.outputFile}")
                            newFile.delete()
                            newFile.createNewFile()
                            newFile.text = responseHtml.toString().trim()
                        } catch (Exception) {
                            throw Exception
                        }
                    }
                }
            }
        }
    }
}

/**
 * The configuration options that can be used at the time of applying the plugin. When the task is triggered in project repository, these plugin options will be read from the gradle file, and the values provided will be considered while running the task.
 *
 * @property maxDepth Defines how deep the plugin should traverse in the project directory to look for template files.
 * @property outputFile Defines what should be the name of the output file that is generated when the task is triggered.
 * @property inputFile Defines the name of the template file from which template should be picked from.
 * @property authHeader Defines the authorization token to send during MJML API call using which the API returns the rendered HTML.
 * @property generateMjml Boolean value which decides whether to generate the mjml file or not. Since the required file is the html that is returned from the API call, the inbetween MJML file is not necessary, hence default value is false.
 */
class RenderMjmlTemplateConfig {
    Integer maxDepth = 100
    String outputFile = "email.html"
    String inputFile = "mjml.ftl"
    String authHeader
    Boolean generateMjml = false
}
