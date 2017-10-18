package demo.domain.generated;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lib.db.Page;
import lib.db.Reference;
import lib.exc.Api400InvalidBodyException;
import lib.exc.Api400InvalidBodyFieldException;
import lib.exc.Api400MissingQueryParameterException;
import lib.vertx.VertxHelper;
import lib.vertx.VertxRestRel;
import lib.vertx.VertxRestResource;
import lib.web.Constants;
import lib.web.LinkRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static lib.web.Constants.ERR_BODY_NOT_JSON;
import static lib.web.Constants.ERR_EXPECTED_TYPE;

/**
 * HTTP API.
 */
public class CoreEntityRestResource implements VertxRestResource
{
    private static final Logger LOG = LoggerFactory.getLogger("web.CoreEntity");
    // @Entity(path="/coreEntities")
    private static final String REL_PATH = "/coreEntities";

    private final Vertx vertx;
    private final LinkRegistry linkRegistry;
    private final CoreEntityService service;

    @Inject
    CoreEntityRestResource(final Vertx vertx, final LinkRegistry linkRegistry, final CoreEntityService service)
    {
        this.vertx = vertx;
        this.linkRegistry = linkRegistry;
        this.service = service;

        this.linkRegistry.registerResource(CoreEntity.class, REL_PATH);
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
         * @Relationship List<CollEntities> collEntities;
        */
        VertxRestRel rel_collEntities = new VertxRestRel("collEntities",
                                                         this::relList_collEntities,
                                                         null,
                                                         this::relPost_collEntities,
                                                         this::relDelete_collEntities,
                                                         this::relPatch_collEntities);
        rels.add(rel_collEntities);

        /*
         * @Relationship(required=false) Optional<Reference<? extends OSEntity>> osEntity;
         */
        VertxRestRel rel_osEntity = new VertxRestRel("osEntity",
                                                     this::relGet_osEntity,
                                                     this::relPut_osEntity,
                                                     null,
                                                     this::relDelete_osEntity,
                                                     null);
        rels.add(rel_osEntity);

        /*
         * @Relationship(required=true) Optional<Reference<? extends MSEntity>> msEntity;
         */
        VertxRestRel rel_msEntity = new VertxRestRel("msEntity",
                                                     this::relGet_msEntity,
                                                     this::relPut_msEntity,
                                                     null,
                                                     null,
                                                     null);
        rels.add(rel_msEntity);

        /*
         * @Relationship(required=true, opposite="coreEntity") Optional<Reference<? extends MSOSEntity> msosEntity;
         */
        VertxRestRel rel_msosEntity = new VertxRestRel("msosEntity",
                                                       this::relGet_msosEntity,
                                                       this::relPut_msosEntity,
                                                       null,
                                                       null,
                                                       null);
        rels.add(rel_msosEntity);

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
        this.vertx.<Page<CoreEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.list(_startId, _count));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseCollection(this.linkRegistry,
                                                  CoreEntity.class,
                                                  _request,
                                                  HttpResponseStatus.OK,
                                                  _result.result(),
                                                  CoreEntity::json);
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

        CoreEntity _dto = CoreEntity.createDTO(this.linkRegistry, _json);

        // blocking database query
        this.vertx.<CoreEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.create(_dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            CoreEntity.class,
                                            _request,
                                            HttpResponseStatus.CREATED,
                                            _result.result(),
                                            CoreEntity::json);
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
        this.vertx.<CoreEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause()); // will be handled by the Vert.x error handler
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

    @Override
    public void itemPatch(final RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        final JsonObject _json = _ctx.getBodyAsJson();
        if (_json == null) {
            throw new Api400InvalidBodyException(ERR_BODY_NOT_JSON);
        }

        CoreEntity _dto = CoreEntity.updateDTO(this.linkRegistry, _json);

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");
        // assuming that ID has been de-urlencoded before being given to us
        if (_id == null || _id.isEmpty()) {
            throw new Api400MissingQueryParameterException("id");
        }

        // blocking database query
        this.vertx.<CoreEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.update(_id, _dto));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
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

    @Override
    public void itemDelete(final RoutingContext _ctx)
    {
        VertxHelper.itemDelete(this.vertx, _ctx, this.service::delete);
    }

    /*
     * @Relationship List<CollEntities> collEntities;
     */
    private void relList_collEntities(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");
        // assuming that ID has been de-urlencoded before being given to us
        if (_id == null || _id.isEmpty()) {
            throw new Api400MissingQueryParameterException("id");
        }
        // startID -- if not specified, default to "0"
        final String _startId = VertxHelper.getStartid(_request.getParam("startId"));
        // count -- if not specified, default to PARAM_LIST_COUNT_DEFAULT, and never exceed PARAM_LIST_COUNT_MAX
        final int _count = VertxHelper.getCount(_request.getParam("count"), LOG);
        // blocking database query
        this.vertx.<Page<CollEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_collEntities_list(_id, _startId, _count));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());  // will be handled by the Vert.x error handler
            } else {
                final Page<CollEntity> _page = _result.result();
                VertxHelper.apiResponseRelCollection(this.linkRegistry,
                                                     CoreEntity.class,
                                                     _id,
                                                     "collEntities",
                                                     _request,
                                                     HttpResponseStatus.OK,
                                                     _page,
                                                     CollEntity::json);
            }
        });
    }

    private void relPost_collEntities(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // URI list
        final String _body = _ctx.getBodyAsString();
        final String[] _refUris = _body.split("\n");
        final List<Reference<? extends CollEntity>> _references = new ArrayList<>(_refUris.length);
        for (int i = 0; i < _refUris.length; i++) {
            _references.add(this.linkRegistry.convertUrlToReference(_refUris[i], CollEntity.class, "[" + i + "]"));
        }

        // blocking database query
        this.vertx.<Page<CollEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_collEntities_add(_id, _references));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                final Page<CollEntity> _page = _result.result();
                VertxHelper.apiResponseRelCollection(this.linkRegistry,
                                                     CoreEntity.class,
                                                     _id,
                                                     "collEntities",
                                                     _request,
                                                     HttpResponseStatus.OK,
                                                     _page,
                                                     CollEntity::json);
            }
        });
    }

    private void relPatch_collEntities(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        final JsonObject _json = _ctx.getBodyAsJson();
        if (_json == null) {
            throw new Api400InvalidBodyException(ERR_BODY_NOT_JSON);
        }

        // Add URI list
        final List<Reference<? extends CollEntity>> _add_rels_refs = new ArrayList<>();
        if (_json.containsKey("add")) {
            try {
                final JsonArray _add_rels_json = _json.getJsonArray("add");
                if (_add_rels_json != null) {
                    final int _size = _add_rels_json.size();
                    for (int _i = 0; _i < _size; _i++) {
                        try {
                            String _add_rel_json_item = _add_rels_json.getString(_i);
                            _add_rels_refs.add(this.linkRegistry.convertUrlToReference(_add_rel_json_item,
                                                                                       CollEntity.class,
                                                                                       "add[" + _i + "]"));
                        } catch (ClassCastException _e) {
                            throw new Api400InvalidBodyFieldException("add[" + _i + "]",
                                                                      _add_rels_json.getValue(_i),
                                                                      ERR_EXPECTED_TYPE.get(URL.class));
                        }
                    }
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("add",
                                                          _json.getValue("add"),
                                                          ERR_EXPECTED_TYPE.get(URL[].class));
            }
        }

        // Delete URI list
        final List<Reference<? extends CollEntity>> _del_rels_refs = new ArrayList<>();
        if (_json.containsKey("delete")) {
            try {
                final JsonArray _del_refs_json = _json.getJsonArray("delete");
                if (_del_refs_json != null) {
                    final int _size = _del_refs_json.size();
                    for (int _i = 0; _i < _size; _i++) {
                        try {
                            String _del_rel_json_item = _del_refs_json.getString(_i);
                            _del_rels_refs.add(this.linkRegistry.convertUrlToReference(_del_rel_json_item,
                                                                                       CollEntity.class,
                                                                                       "delete[" + _i + "]"));
                        } catch (ClassCastException _e) {
                            throw new Api400InvalidBodyFieldException("delete[" + _i + "]",
                                                                      _del_refs_json.getValue(_i),
                                                                      ERR_EXPECTED_TYPE.get(URL.class));
                        }
                    }
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("delete",
                                                          _json.getValue("delete"),
                                                          ERR_EXPECTED_TYPE.get(URL[].class));
            }
        }

        // blocking database query
        this.vertx.<Page<CollEntity>>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_collEntities_patch(_id, _add_rels_refs, _del_rels_refs));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                final Page<CollEntity> _page = _result.result();
                VertxHelper.apiResponseRelCollection(this.linkRegistry,
                                                     CoreEntity.class,
                                                     _id,
                                                     "collEntities",
                                                     _request,
                                                     HttpResponseStatus.OK,
                                                     _page,
                                                     CollEntity::json);
            }
        });
    }

    private void relDelete_collEntities(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_collEntities_deleteAll(_id);
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

    private void relGet_osEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<OSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_osEntity_get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            OSEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            OSEntity::json);
            }
        });
    }

    private void relPut_osEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // URI in body
        final String _body = _ctx.getBodyAsString();
        Reference<? extends OSEntity> _reference =
                this.linkRegistry.convertUrlToReference(_body.trim(), OSEntity.class, "[0]");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_osEntity_set(_id, _reference);
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

    private void relDelete_osEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_osEntity_delete(_id);
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

    private void relGet_msEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<MSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_msEntity_get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
            } else {
                VertxHelper.apiResponseItem(this.linkRegistry,
                                            MSEntity.class,
                                            _request,
                                            HttpResponseStatus.OK,
                                            _result.result(),
                                            MSEntity::json);
            }
        });
    }

    private void relPut_msEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // URI in body
        final String _body = _ctx.getBodyAsString();
        Reference<? extends MSEntity> _reference =
                this.linkRegistry.convertUrlToReference(_body.trim(), MSEntity.class, "[0]");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_msEntity_set(_id, _reference);
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

    private void relGet_msosEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // blocking database query
        this.vertx.<MSOSEntity>executeBlocking(_fut -> {
            _fut.complete(this.service.rel_msosEntity_get(_id));
        }, _result -> {
            if (_result.failed()) {
                _ctx.fail(_result.cause());
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

    private void relPut_msosEntity(RoutingContext _ctx)
    {
        final HttpServerRequest _request = _ctx.request();

        // mandatory path parameter -- id
        final String _id = _ctx.pathParam("id");

        // URI in body
        final String _body = _ctx.getBodyAsString();
        Reference<? extends MSOSEntity> _reference =
                this.linkRegistry.convertUrlToReference(_body.trim(), MSOSEntity.class, "[0]");

        // blocking database query
        this.vertx.<Void>executeBlocking(_fut -> {
            this.service.rel_msosEntity_set(_id, _reference);
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
