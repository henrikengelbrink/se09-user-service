package se09.user.service.exceptions

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.server.exceptions.ExceptionHandler;
import se09.user.service.utils.GlobalLogger
import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = [APIException::class, ExceptionHandler::class])
class APIExceptionHandler : ExceptionHandler<APIException, HttpResponse<Any>> {

    @Override
    override fun handle(request: HttpRequest<Any>, exception: APIException): HttpResponse<Any> {
        GlobalLogger.error(mapOf(
                "exception" to exception::class.java.name,
                "code" to exception.code.name
        ))
        return HttpResponse.status(exception.code.httpCode)
    }

}

@Produces
@Singleton
@Requires(classes = [HttpClientResponseException::class, ExceptionHandler::class])
class HttpClientResponseExceptionHandler : ExceptionHandler<HttpClientResponseException, HttpResponse<Any>> {

    @Override
    override fun handle(request: HttpRequest<Any>, exception: HttpClientResponseException): HttpResponse<Any> {
        GlobalLogger.error(mapOf(
                "exception" to exception::class.java.name,
                "status" to exception.status
        ))
        return HttpResponse.status(exception.status)
    }

}