package pl.oakfusion

import org.apache.http.client.methods.*
import org.apache.http.impl.client.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class DownloadFileHttpGetPlugin implements Plugin<Project>{
    @Override
    public void apply(Project project) {
        Map<String, String> urlsAndTokens = getUrlsAndTokens(project.getProperties())
        urlsAndTokens.each {
            String url = it.getKey()
            String deployToken = it.getValue()
            downloadResponse(url, 'DEPLOY-TOKEN', deployToken)
        }
    }

    private Map<String, String> getUrlsAndTokens(Map<String, ?> properties){
        Map<String, String> urlsAndTokens = new HashMap<String, String>();
        properties.each {
            String key = it.getKey()
            if(key.startsWith("downloadURL")){
                String deployToken = properties.get(new String('downloadDeployToken'+key.substring(11)))
                if(deployToken != null){
                    String url = it.getValue()
                    urlsAndTokens.put(url, deployToken)
                }
            }
        }
        return urlsAndTokens
    }

    private void downloadResponse(String url, String headerName, String headerValue){
        def get = new HttpGet(url)
        get.addHeader(headerName, headerValue)
        def client = HttpClientBuilder.create().build()
        def fileName = url.substring(url.lastIndexOf('/')+1)
        if(fileName.isBlank()) {
            fileName = 'New File.txt'
        }
        try {
            File file = new File(fileName)
            if(!file.exists())
                file.createNewFile()
            def response = client.execute(get)
            def writer = new BufferedWriter(new FileWriter(file))
            writer.write(response.getEntity().getContent().getText())
            writer.close()
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
