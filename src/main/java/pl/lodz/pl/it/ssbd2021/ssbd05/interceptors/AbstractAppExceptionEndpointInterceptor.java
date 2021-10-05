package pl.lodz.pl.it.ssbd2021.ssbd05.interceptors;

import pl.lodz.pl.it.ssbd2021.ssbd05.dto.ErrorDTO;
import pl.lodz.pl.it.ssbd2021.ssbd05.exceptions.*;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.ws.rs.core.Response;

/**
 * Interceptor mapujący wyjątki aplikacyjne
 */
public class AbstractAppExceptionEndpointInterceptor {

    /**
     * Metoda mapująca ewentualny wyjątek aplikacyjny na odpowiedni status HTTP
     * @param ictx kontekst wywołania metody
     * @return obiekt zwracany przez metodę
     * @throws Exception ewentualny wyjątek
     */
    @AroundInvoke
    public Object intercept(InvocationContext ictx) throws Exception {
        try {
            return ictx.proceed();
        } catch (UniqueConstraintAppException ue) {
            return Response.status(Response.Status.CONFLICT).entity(ErrorDTO.of(ue.getKey())).build();
        } catch (UserNotFoundAppException | EntertainerNotFoundException | OfferNotFoundException nfe) {
            return Response.status(Response.Status.NOT_FOUND).entity(ErrorDTO.of(nfe.getKey())).build();
        } catch (OptimisticLockAppException ole) {
            return Response
                    .status(Response.Status.PRECONDITION_FAILED).entity(ErrorDTO.of(ole.getKey())).build();
        } catch (DatabaseErrorAppException dbe) {
            return Response
                    .status(Response.Status.BAD_GATEWAY).entity(ErrorDTO.of(dbe.getKey())).build();
        } catch (AbstractAppException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(ErrorDTO.of(e.getKey())).build();
        }
    }
}
