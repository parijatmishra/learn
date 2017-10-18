package demo.domain.generated;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lib.db.Page;
import lib.db.Reference;
import lib.exc.Api400InvalidBodyException;
import lib.exc.Api400MissingQueryParameterException;
import lib.vertx.VertxHelper;
import lib.vertx.VertxRestRel;
import lib.vertx.VertxRestResource;
import lib.web.Constants;
import lib.web.LinkRegistry;
import lib.web.UriContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static lib.web.Constants.ERR_BODY_NOT_JSON;

/**
 *
 */
public class MSOSEntityRestResource implements VertxRestResource
{
    private static final Logger LOG = LoggerFactory.getLogger("web.MSOSEntity");
    // @Entity(path="/osEntities")
    private static final String REL_PATH = "/msosEntities";

    private final Vertx vertx;
    private final LinkRegistry linkRegistry;
    private final MSOSEntityService service;

    @Inject
    public MSOSEntityRestResource(final Vertx vertx, final LinkRegistry linkRegistry, final MSOSEntityService service)
    {
        this.vertx = vertx;
        this.linkRegistry = linkRegistry;
        this.service = service;

        this.linkRegistry.registerResource(MSOSEntity.class, REL_PATH);
    }

    @Override
    public String getCollectionResourceRelativePath()
    {
        return REL_PATH;
    }

    @Override
    public List<VertxRestRel> getRels()
    {
        List<VertxRestRel> rels = new ArrayList<>();

        /*
         * @Relationship(required=false, opposite="msosEntity") Optional<Reference<? extends CoreEntity>> coreEntity;
         */
        VertxRestRel rel_coreEntity = new VertxRestRel("coreEntity",
                                                       this::relGet_coreEntity,
                                                       this::relPut_coreEntity,
                                                       null,
                                                       null,
                                                       null);
        rels.add(rel_coreEntity);

        return rels;
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
        this.vertx.<Page<MSOSEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.list(_startId, _count));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseCollection(this.linkRegistry,
                                                  MSOSEntity.class,
                                                  _request,
                                                  HttpResponseStatus.OK,
                                                  _result.result(),
                                                  MSOSEntity::json);
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

        MSOSEntity _dto = MSOSEntity.createDTO(this.linkRegistry, _json);

        // blocking database query
        this.vertx.<MSOSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.create(_dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            MSOSEntity.class,
                                            _request,
                                            HttpResponseStatus.CREATED,
                                            _result.result(),
                                            MSOSEntity::json);
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
        this.vertx.<MSOSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause()); // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            MSOSEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            MSOSEntity::json);
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

        MSOSEntity _dto = MSOSEntity.updateDTO(this.linkRegistry, _json);

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");
        // assuming that ID has been de-urlencoded before being given to us
        if (_id == null || _id.isEmpty()) {
            throw new Api400MissingQueryParameterException("id");
        }

        // blocking database query
        this.vertx.<MSOSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.update(_id, _dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            MSOSEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            MSOSEntity::json);

            }
        });
    }

    @Override
    public void itemDelete(final RoutingContext _ctx)
    {
        VertxHelper.itemDelete(this.vertx, _ctx, this.service::delete);
    }

    private void relGet_coreEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<CoreEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_coreEntity_get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            CoreEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            CoreEntity::json);
            }
        });
    }

    private void relPut_coreEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // URI in body
        final String _body = _ctx.getBodyAsString();
        Reference<? extends CoreEntity> _reference = this.linkRegistry.convertUrlToReference(_body.trim(),
                                                                                             CoreEntity.class,
                                                                                             "[0]");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_coreEntity_set(_id, _reference);
            _fut.complete();
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                final JsonObject apiResponse = new JsonObject();
                apiResponse.put("_meta", VertxHelper.meta(HttpResponseStatus.OK));
                final HttpServerResponse httpServerResponse = _request.response();
                httpServerResponse.putHeader(Constants.HEADER_CONTENT_TYPE, Constants.MIME_APPLICATION_JSON);
                httpServerResponse.end(apiResponse.encodePrettily());
            }
        });
    }
}
