import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import presentation.*;
import util.CorsFilter;


//Defines the base URI for all resource URIs.
@ApplicationPath("rest")
//The java class declares root resource and provider classes
public class SWAApplication extends Application{

    private final Set<Class<?>> classes;

    public SWAApplication() {
        HashSet<Class<?>> c = new HashSet<Class<?>>();

        c.add(RESTauth.class);
        c.add(RESTaziende.class);
        c.add(RESTstudenti.class);
        c.add(RESTofferte.class);

        //c.add(CorsFilter.class); //FILTER CORS

        c.add(JacksonJsonProvider.class);

        classes = Collections.unmodifiableSet(c);
    }

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }
}