package demo.domain.generated;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lib.db.Page;
import lib.exc.Api400InvalidBodyException;
import lib.exc.Api400MissingQueryParameterException;
import lib.vertx.VertxHelper;
import lib.vertx.VertxRestRel;
import lib.vertx.VertxRestResource;
import lib.web.LinkRegistry;
import lib.web.UriContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;

import static lib.web.Constants.ERR_BODY_NOT_JSON;

/**
 *
 */
public class CollEntityRestResource implements VertxRestResource
{
    private static final Logger LOG = LoggerFactory.getLogger("web.CollEntity");
    // @Entity(path="/collEntities")
    private static final String REL_PATH = "/collEntities";

    private final Vertx vertx;
    private final LinkRegistry linkRegistry;
    private final CollEntityService service;

    @Inject
    public CollEntityRestResource(final Vertx vertx, final LinkRegistry linkRegistry, final CollEntityService service)
    {
        this.vertx = vertx;
        this.linkRegistry = linkRegistry;
        this.service = service;

        this.linkRegistry.registerResource(CollEntity.class, REL_PATH);
    }

    @Override
    public String getCollectionResourceRelativePath()
    {
        return REL_PATH;
    }

    @Override
    public List<VertxRestRel> getRels()
    {
        return null;
    }

    @Override
    public void init()
    {
        this.service.init();
    }

    @Override
    public void collectionGet(final RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // startID -- if not specified, default to "0"
        final String _startId = VertxHelper.getStartid(_request.getParam("startId"));
        // count -- if not specified, default to PARAM_LIST_COUNT_DEFAULT, and never exceed PARAM_LIST_COUNT_MAX
        final int _count = VertxHelper.getCount(_request.getParam("count"), LOG);
        // blocking database query
        this.vertx.<Page<CollEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.list(_startId, _count));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseCollection(this.linkRegistry,
                                                  CollEntity.class,
                                                  _request,
                                                  HttpResponseStatus.OK,
                                                  _result.result(),
                                                  CollEntity::json);
            }
        });
    }

    @Override
    public void collectionPost(final RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        final JsonObject _json = _ctx.getBodyAsJson();
        if (_json == null) {
            throw new Api400InvalidBodyException(ERR_BODY_NOT_JSON);
        }

        CollEntity _dto = CollEntity.createDTO(this.linkRegistry, _json);

        // blocking database query
        this.vertx.<CollEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.create(_dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            CollEntity.class,
                                            _request,
                                            HttpResponseStatus.CREATED,
                                            _result.result(),
                                            CollEntity::json);
            }
        });
    }

    @Override
    public void itemGet(final RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");
        // assuming that ID has been de-urlencoded before being given to us
        if (_id == null || _id.isEmpty()) {
            throw new Api400MissingQueryParameterException("id");
        }
        this.vertx.<CollEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause()); // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            CollEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            CollEntity::json);
            }
        });
    }

    @Override
    public void itemPatch(final RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();
        final UriContext _uriContext = VertxHelper.fromHttpServerRequest(_request);

        final JsonObject _json = _ctx.getBodyAsJson();
        if (_json == null) {
            throw new Api400InvalidBodyException(ERR_BODY_NOT_JSON);
        }

        CollEntity _dto = CollEntity.updateDTO(this.linkRegistry, _json);

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");
        // assuming that ID has been de-urlencoded before being given to us
        if (_id == null || _id.isEmpty()) {
            throw new Api400MissingQueryParameterException("id");
        }

        // blocking database query
        this.vertx.<CollEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.update(_id, _dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            CollEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            CollEntity::json);

            }
        });
    }

    @Override
    public void itemDelete(final RoutingContext _ctx)
    {
        VertxHelper.itemDelete(this.vertx, _ctx, this.service::delete);
    }
}
