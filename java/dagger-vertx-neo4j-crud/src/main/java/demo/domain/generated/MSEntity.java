package demo.domain.generated;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lib.exc.Api400InvalidBodyFieldException;
import lib.exc.Api400MissingBodyFieldException;
import lib.web.LinkRegistry;
import lib.web.UriContext;
import org.neo4j.driver.v1.Value;

import java.util.Optional;

import static lib.web.Constants.ERR_EXPECTED_TYPE;

/**
 *
 */
// @Entity(path="/msEntities")
public class MSEntity
{
    // @Entity class MSEntity {}
    private static final JsonArray typesJsonArray = new JsonArray().add("MSEntity");

    // generated ID
    String _id;
    // @Searchable @Required String name;
    Optional<String> name;


    public static JsonObject json(final LinkRegistry linkRegistry, final UriContext _uriContext, final MSEntity _dto)
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
        final String _self = linkRegistry.getResourceItemUrl(MSEntity.class, _uriContext, _dto._id);
        _links.put("self", new JsonObject().put("href", _self));
        _itemJsonObject.put("_links", _links);

        return _itemJsonObject;
    }

    public static MSEntity createDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        final MSEntity _dto = updateDTO(linkRegistry, _json);

        // @Searchable @Required String name;
        if (_dto.name == null) {
            throw new Api400MissingBodyFieldException("name");
        }

        return _dto;
    }

    public static MSEntity updateDTO(final LinkRegistry linkRegistry, final JsonObject _json)
    {
        /*
         * A null field means the client did not specify a particular field in the JSON it sent.
         * A Optional.empty() means that the client did specify that field, and set it to null.
         */
        final MSEntity _dto = new MSEntity();

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

        return _dto;
    }

    public static MSEntity dto(final Value _node)
    {
        // Return value
        MSEntity _item = new MSEntity();
        // generated ID
        _item._id = _node.get("_id").asString();
        // @Searchable @Required String name;
        _item.name = Optional.of(_node.get("name").asString());

        return _item;
    }
}
