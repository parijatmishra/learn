package demo.domain.generated;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lib.db.Reference;
import lib.exc.Api400InvalidBodyFieldException;
import lib.exc.Api400MissingBodyFieldException;
import lib.web.LinkRegistry;
import lib.web.UriContext;
import org.neo4j.driver.v1.Value;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static lib.web.Constants.ERR_EXPECTED_TYPE;

/**
 *
 */
// @Entity(path="/coreEntities")
class CoreEntity
{
    // @Entity class CoreEntity {}
    private static final JsonArray typesJsonArray = new JsonArray().add("CoreEntity");

    // generated ID
    String _id;
    // @Searchable @Required String name;
    Optional<String> name;
    // @Searchable @Nullable Boolean someProp;
    Optional<Boolean> someProp;
    // List<Long> anotherProp;
    // Lists are never null, and are empty by default.
    Optional<List<Long>> anotherProp;

    // @Relationship
    Optional<List<Reference<? extends CollEntity>>> collEntities;
    // @Relationship(required=false)
    Optional<Reference<? extends OSEntity>> osEntity;
    // @Relationship(required=true)
    Optional<Reference<? extends MSEntity>> msEntity;
    // @Relationship(required=true, opposite="coreEntitiy")
    Optional<Reference<? extends MSOSEntity>> msosEntity;

    public static JsonObject json(final LinkRegistry linkRegistry, final UriContext _uriContext, final CoreEntity _dto)
    {
        final JsonObject _itemJsonObject = new JsonObject();
        // always let the client know all the types this entity can be referred to as
        _itemJsonObject.put("_types", typesJsonArray);
        // generated ID
        _itemJsonObject.put("_id", _dto._id);

        /*
         * Properties
         */
        // private String name
        _itemJsonObject.put("name", _dto.name.get());
        // @Nullable private Boolean someProp
        if (_dto.someProp.isPresent()) {
            _itemJsonObject.put("someProp", _dto.someProp.get());
        }
        // private List<Long> anotherProp;
        final JsonArray _anotherPropJson = new JsonArray();
        for (Long _anotherPropJson_item : _dto.anotherProp.get()) {
            _anotherPropJson.add(_anotherPropJson_item);
        }
        _itemJsonObject.put("anotherProp", _anotherPropJson);

        /*
         * Relationships
         */
        final JsonObject _links = new JsonObject();
        final String _rel_self = linkRegistry.getResourceItemUrl(CoreEntity.class, _uriContext, _dto._id);
        _links.put("self", new JsonObject().put("href", _rel_self));

        // @Relationship List<Reference<CollEntity>> collEntities;
        final String _rel_collEntities = _rel_self + "/collEntities";
        _links.put("collEntities", new JsonObject().put("href", _rel_collEntities));
        // @Relationship(required=false) Optional<Reference<? extends OSEntity>> osEntity;
        final String _rel_osEntity = _rel_self + "/osEntity";
        _links.put("osEntity", new JsonObject().put("href", _rel_osEntity));
        // @Relationshup(required=true) Optional<Reference<? extends MSEntity>> msEntity
        final String _rel_msEntity = _rel_self + "/msEntity";
        _links.put("msEntity", new JsonObject().put("href", _rel_msEntity));
        // @Relationship(required=true, opposite="coreEntitiy") Optional<Reference<? extends MSOSEntity>> msosEntity;
        final String _rel_msosEntity = _rel_self + "/msosEntity";
        _links.put("msosEntity", new JsonObject().put("href", _rel_msosEntity));

        _itemJsonObject.put("_links", _links);

        return _itemJsonObject;
    }

    public static CoreEntity createDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        final CoreEntity _dto = updateDTO(linkRegistry, _json);

        // @Searchable @Required Optional<String> name;
        if (_dto.name == null) {
            throw new Api400MissingBodyFieldException("name");
        }
        // @Relationship(required=true) Optional<Reference<? extends MSEntity>> msEntity
        if (_dto.msEntity == null) {
            throw new Api400MissingBodyFieldException("msEntity");
        }
        // @Relationship(required=true, opposite="coreEntitiy") Optional<Reference<? extends MSOSEntity>> msosEntity;
        if (_dto.msosEntity == null) {
            throw new Api400MissingBodyFieldException("msosEntity");
        }

        return _dto;
    }

    public static CoreEntity updateDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        /*
         * A null field means the client did not specify a particular field in the JSON it sent.
         * A Optional.empty() means that the client did specify that field, and set it to null.
         */
        final CoreEntity _dto = new CoreEntity();

        // @Searchable @Required String name;
        if (_json.containsKey("name")) {
            try {
                // name cannot be set to null
                _dto.name = Optional.of(_json.getString("name"));
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("name",
                                                          _json.getValue("name"),
                                                          ERR_EXPECTED_TYPE.get(String.class));
            } catch (NullPointerException _e) {
                throw new Api400InvalidBodyFieldException("name", null, "non-null value");
            }
        }

        // @Searchable @Nullable Boolean someProp;
        if (_json.containsKey("someProp")) {
            try {
                _dto.someProp = Optional.ofNullable(_json.getBoolean("someProp"));
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("someProp",
                                                          _json.getValue("someProp"),
                                                          ERR_EXPECTED_TYPE.get(Boolean.class));
            }
        }

        // List<Long> anotherProp;
        // Lists are never null, and are empty by default.
        if (_json.containsKey("anotherProp")) {
            try {
                final JsonArray _json_arr = _json.getJsonArray("anotherProp");
                if (_json_arr != null) {
                    final int _size = _json_arr.size();
                    List<Long> _val_arr = new ArrayList<>(_size);
                    for (int _i = 0; _i < _size; _i++) {
                        try {
                            Long _val = _json_arr.getLong(_i);
                            _val_arr.add(_val);
                        } catch (ClassCastException _e) {
                            throw new Api400InvalidBodyFieldException("anotherProp[" + _i + "]",
                                                                      _json_arr.getValue(_i),
                                                                      ERR_EXPECTED_TYPE.get(Long.class));
                        }
                    }
                    _dto.anotherProp = Optional.of(_val_arr);
                } else {
                    _dto.anotherProp = Optional.empty(); // caller specified { "anotherProp": null }
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("anotherProp",
                                                          _json.getValue("anotherProp"),
                                                          ERR_EXPECTED_TYPE.get(Long[].class));
            }
        }

        // @Relationship List<Reference<CollEntity>> collEntities;
        if (_json.containsKey("collEntities")) {
            try {
                final JsonArray _json_arr = _json.getJsonArray("collEntities");
                if (_json_arr != null) {
                    final int _size = _json_arr.size();
                    List<Reference<? extends CollEntity>> _val_arr = new ArrayList<>(_size);
                    for (int _i = 0; _i < _size; _i++) {
                        try {
                            String _val = _json_arr.getString(_i);
                            final Reference<CollEntity> _ref =
                                    linkRegistry.convertUrlToReference(_val,
                                                                       CollEntity.class,
                                                                       "collEntities[" + _i + "]");
                            _val_arr.add(_ref);
                        } catch (ClassCastException _e) {
                            throw new Api400InvalidBodyFieldException("collEntities[" + _i + "]",
                                                                      _json_arr.getValue(_i),
                                                                      ERR_EXPECTED_TYPE.get(URL.class));
                        }
                    }
                    _dto.collEntities = Optional.of(_val_arr);
                } else {
                    _dto.collEntities = Optional.empty(); // caller specified { "collEntities": null }
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("collEntities",
                                                          _json.getValue("collEntities"),
                                                          ERR_EXPECTED_TYPE.get(URL[].class));
            }
        }

        // @Relationship(required=false) Optional<Reference<? extends OSEntity>> osEntity;
        if (_json.containsKey("osEntity")) {
            try {
                final String _val = _json.getString("osEntity");
                if (_val != null) {
                    Reference<? extends OSEntity> _ref =
                            linkRegistry.convertUrlToReference(_val,
                                                               OSEntity.class,
                                                               "osEntity");
                    _dto.osEntity = Optional.of(_ref);
                } else {
                    _dto.osEntity = Optional.empty();
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("osEntity",
                                                          _json.getValue("osEntity"),
                                                          ERR_EXPECTED_TYPE.get(URL.class));
            }
        }

        // @Relationship(required=true) Optional<Reference<? extends MSEntity>> msEntity;
        if (_json.containsKey("msEntity")) {
            try {
                final String _val = _json.getString("msEntity");
                if (_val != null) {
                    final Reference<? extends MSEntity> _ref =
                            linkRegistry.convertUrlToReference(_val,
                                                               MSEntity.class,
                                                               "msEntity");
                    _dto.msEntity = Optional.of(_ref);
                } else {
                    throw new Api400InvalidBodyFieldException("msEntity",
                                                              "null",
                                                              "non-null reference to an MSEntity resource");
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("msEntity",
                                                          _json.getValue("msEntity"),
                                                          ERR_EXPECTED_TYPE.get(URL.class));
            }
        }

        // @Relationship(required=true, opposite="coreEntitiy") Optional<Reference<? extends MSOSEntity>> msosEntity;
        if (_json.containsKey("msosEntity")) {
            try {
                final String _val = _json.getString("msosEntity");
                if (_val != null) {
                    final Reference<? extends MSOSEntity> _ref =
                            linkRegistry.convertUrlToReference(_val,
                                                               MSOSEntity.class,
                                                               "msEntity");
                    _dto.msosEntity = Optional.of(_ref);
                } else {
                    throw new Api400InvalidBodyFieldException("msosEntity",
                                                              "null",
                                                              "non-null reference to an MSOSEntity resource");
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("msosEntity",
                                                          _json.getValue("msosEntity"),
                                                          ERR_EXPECTED_TYPE.get(URL.class));
            }
        }

        return _dto;
    }


    public static CoreEntity dto(final Value _node)
    {
        // Return value
        CoreEntity _item = new CoreEntity();
        // generated ID
        _item._id = _node.get("_id").asString();
        // @Searchable @Required String name;
        _item.name = Optional.of(_node.get("name").asString());
        // @Searchable @Nullable Boolean someProp;
        final Value _somePropValue = _node.get("someProp");
        _item.someProp = Optional.ofNullable(_somePropValue.isNull() ? null : _somePropValue.asBoolean());
        // private List<Long> anotherProp
        final Value _anotherPropValue = _node.get("anotherProp");
        // List<Long> anotherProp;
        // Lists are never null, and are empty by default.
        if (!_anotherPropValue.isNull() && !_anotherPropValue.isEmpty()) {
            _item.anotherProp = Optional.of(_anotherPropValue.asList(v -> v.asLong()));
        } else {
            // database may not have this value set and may return a null -- if the db Node was
            // created by an earlier version of the code where this property may not have existed.
            _item.anotherProp = Optional.of(Collections.emptyList());
        }

        return _item;
    }
}
