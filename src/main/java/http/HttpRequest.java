package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private RequestLine requestLine;
    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> params = new HashMap<String, String>();

    public HttpRequest(InputStream in) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            log.debug("line: {}", line);
            if (line == null) {
                return;
            }

            requestLine = new RequestLine(line);

            line = br.readLine();
            while(!"".equals(line) && line != null) {
                log.debug("header : {}", line);
                String[] tokens = line.split(":");
                headers.put(tokens[0].trim(), tokens[1].trim());
                line = br.readLine();
            }

            if (getMethod().isPost()) {
                String body = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
                params = HttpRequestUtils.parseQueryString(body);
            } else {
                params = requestLine.getParams();
            }

        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public HttpMethod getMethod(){
        return requestLine.getMethod();
    }
    public String getPath(){
        return requestLine.getPath();
    }
    public String getHeader(String name){
        return headers.get(name);
    }
    public String getParameter(String name){
        return params.get(name);
    }
}
