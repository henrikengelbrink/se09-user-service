package se09.user.service.exceptions

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = [Exception::class, ExceptionHandler::class])
class APIExceptionHandler : ExceptionHandler<APIException, HttpResponse<Any>> {

    override fun handle(request: HttpRequest<Any>, exception: APIException): HttpResponse<Any> {
        println("########### APIException ${exception.code.name} ${exception.code.httpCode.code}")
        return HttpResponse.status(exception.code.httpCode)
    }

}