import java.lang.reflect.Method;

import play.Application;
import play.GlobalSettings;
import play.libs.F.Promise;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Result;

import com.aerospike.session.SessionStore;

public class Global extends GlobalSettings {
    private SessionStore store;

    /*
     * (non-Javadoc)
     *
     * @see play.GlobalSettings#onRequest(play.mvc.Http.Request,
     * java.lang.reflect.Method)
     */
    @Override
    @SuppressWarnings("rawtypes")
    public Action onRequest(Request request, Method actionMethod) {

        return new Action.Simple() {
            @Override
            public Promise<Result> call(Context ctx) throws Throwable {
                if (!store.exists()) {
                    store.create();
                }
                return delegate.call(ctx);
            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see play.GlobalSettings#onStart(play.Application)
     */
    @Override
    public void onStart(Application application) {
        store = application.injector().instanceOf(SessionStore.class);
        super.beforeStart(application);
    }

}
