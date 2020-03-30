package se09.user.service.exceptions

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.server.exceptions.ExceptionHandler;
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import se09.user.service.controller.AuthController
import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = [APIException::class, ExceptionHandler::class])
class APIExceptionHandler : ExceptionHandler<APIException, HttpResponse<Any>> {

    private val LOG: Logger = LoggerFactory.getLogger(APIExceptionHandler::class.java)

    @Override
    override fun handle(request: HttpRequest<Any>, exception: APIException): HttpResponse<Any> {
        LOG.warn("########### APIException ${exception.code.name} ${exception.code.httpCode.code}")
        return HttpResponse.status(exception.code.httpCode)
    }

}

@Produces
@Singleton
@Requires(classes = [HttpClientResponseException::class, ExceptionHandler::class])
class HttpClientResponseExceptionHandler : ExceptionHandler<HttpClientResponseException, HttpResponse<Any>> {

    private val LOG: Logger = LoggerFactory.getLogger(APIExceptionHandler::class.java)

    @Override
    override fun handle(request: HttpRequest<Any>, exception: HttpClientResponseException): HttpResponse<Any> {
        LOG.warn("########### HttpClientResponseException ${exception.status}")
        return HttpResponse.status(exception.status)
    }

}
