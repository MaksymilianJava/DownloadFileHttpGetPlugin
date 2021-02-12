package pl.oakfusion

import org.apache.http.client.methods.*
import org.apache.http.impl.client.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class DownloadFileHttpGetPlugin implements Plugin<Project>{
    @Override
    public void apply(Project project) {
        Map<String, String> properties = project.getProperties()
        String url = properties.get('downloadURL')
        String deployToken = properties.get('downloadDeployToken')
        downloadFile(url, 'DEPLOY-TOKEN', deployToken)
        println("applied download")
    }
    public static void downloadFile(String url, String headerName, String headerValue){
        def get = new HttpGet(url)
        get.addHeader(headerName, headerValue)
        def client = HttpClientBuilder.create().build()
        def response = client.execute(get)
        def fileName = url.substring(url.lastIndexOf('/')+1)
        def writer = new BufferedWriter(new FileWriter(fileName))
        writer.write(response.getEntity().getContent().getText())
        writer.close()
    }
}
