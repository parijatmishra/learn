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
import java.util.Optional;

import static lib.web.Constants.ERR_EXPECTED_TYPE;

/**
 *
 */
// @Entity(path="/osEntities")
public class MSOSEntity
{
    // @Entity class OSEntity {}
    private static final JsonArray typesJsonArray = new JsonArray().add("MSOSEntity");

    // generated ID
    String _id;
    // @Searchable @Required String name;
    Optional<String> name;
    // @Relationship(required=false, opposite="msosEntity")
    Optional<Reference<? extends CoreEntity>> coreEntity;

    public static JsonObject json(final LinkRegistry linkRegistry, final UriContext _uriContext, final MSOSEntity _dto)
    {
        final JsonObject _itemJsonObject = new JsonObject();
        // always let the client know all the types this entity can be referred to as
        _itemJsonObject.put("_types", typesJsonArray);

        /*
         * Properties
         */
        // generated ID
        _itemJsonObject.put("_id", _dto._id);
        // @Searchable @Required String name;
        _itemJsonObject.put("name", _dto.name.get());

        /*
         * Relationships
         */
        final JsonObject _links = new JsonObject();
        final String _rel_self = linkRegistry.getResourceItemUrl(MSOSEntity.class, _uriContext, _dto._id);
        _links.put("self", new JsonObject().put("href", _rel_self));
        // @Relationship(required=false, opposite="msosEntity") Optional<Reference<? extends CoreEntity>> coreEntity;
        final String _rel_coreEntity = _rel_self + "/coreEntity";
        _links.put("coreEntity", new JsonObject().put("href", _rel_coreEntity));

        _itemJsonObject.put("_links", _links);
        return _itemJsonObject;
    }

    public static MSOSEntity createDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        final MSOSEntity _dto = updateDTO(linkRegistry, _json);

        // @Searchable @Required String name;
        if (_dto.name == null) {
            throw new Api400MissingBodyFieldException("name");
        }

        return _dto;
    }

    public static MSOSEntity updateDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        /*
         * A null field means the client did not specify a particular field in the JSON it sent.
         * A Optional.empty() means that the client did specify that field, and set it to null.
         */
        final MSOSEntity _dto = new MSOSEntity();

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

        // @Relationship(required=false, opposite="msosEntity") Optional<Reference<? extends CoreEntity>> coreEntity;
        if (_json.containsKey("coreEntity")) {
            try {
                final String _val = _json.getString("coreEntity");
                if (_val != null) {
                    Reference<? extends CoreEntity> _ref =
                            linkRegistry.convertUrlToReference(_val,
                                                               CoreEntity.class,
                                                               "coreEntity");
                    _dto.coreEntity = Optional.of(_ref);
                } else {
                    throw new Api400InvalidBodyFieldException("coreEntity",
                                                              null,
                                                              "non-null value");
                }
            } catch (ClassCastException _e) {
                throw new Api400InvalidBodyFieldException("coreEntity",
                                                          _json.getValue("coreEntity"),
                                                          ERR_EXPECTED_TYPE.get(URL.class));
            }
        }

        return _dto;
    }

    public static MSOSEntity dto(final Value _node)
    {
        // Return value
        MSOSEntity _item = new MSOSEntity();
        // generated ID
        _item._id = _node.get("_id").asString();
        // @Searchable @Required String name;
        _item.name = Optional.of(_node.get("name").asString());

        return _item;
    }
}
