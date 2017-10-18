package demo.domain.generated;

import lib.db.IDGenerator;
import lib.db.Page;
import lib.db.Reference;
import lib.exc.Api400InvalidReferenceException;
import lib.exc.Api404MissingRelationshipException;
import lib.exc.Api404MissingResourceException;
import lib.exc.Api409ResourceInUseException;
import lib.neo4j.Neo4JHelper;
import lib.web.LinkRegistry;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.*;

/**
 *
 */
public class MSOSEntityRepository
{
    private static final Logger LOG = LoggerFactory.getLogger("db.MSOSEntity");

    private final IDGenerator idGenerator;
    private final LinkRegistry linkRegistry;

    @Inject
    MSOSEntityRepository(final IDGenerator idGenerator, final LinkRegistry linkRegistry)
    {
        this.idGenerator = idGenerator;
        this.linkRegistry = linkRegistry;
    }

    Page<MSOSEntity> list(final Transaction _transaction, final String _startId, final int _count)
    {
        LOG.debug("list: startId={} count={}", _startId, _count);

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("startId", _startId);
        // attempt to fetch one more than asked -- this is helpful for pagination
        _params.put("count", _count + 1);

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity) WHERE node._id >= $startId RETURN node LIMIT $count",
                _params);
        final List<MSOSEntity> _dtos = _result.list(rec -> MSOSEntity.dto(rec.get("node")));
        final String _nextStartId = _dtos.size() > _count ? _dtos.remove(_count)._id : null;
        final Page<MSOSEntity> _ret = new Page<>(_startId, _count, _dtos, _nextStartId);

        LOG.debug("list: {}", _ret);
        return _ret;
    }

    MSOSEntity get(final Transaction _transaction, final String _id)
    {
        LOG.debug("get: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run("MATCH (node:MSOSEntity {_id:$_id}) RETURN node", _params);

        try {
            final Record _record = _result.single(); // consumes the result completely
            final Value _node = _record.get("node");
            return MSOSEntity.dto(_node);
        } catch (NoSuchRecordException e) {
            throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
        }
    }

    String createNode(final Transaction _transaction, final MSOSEntity _dto)
    {
        // generated ID
        final String _id = this.idGenerator.getNextId("MSOSEntity");
        LOG.debug("createNode: generatedId={}", _id);

        /*
         * Properties
         */
        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        // @Searchable @Required String name;
        _params.put("name", _dto.name.get());

        final StatementResult _result = _transaction.run("CREATE (node:MSOSEntity $params)",
                                                         Collections.singletonMap("params", _params));
        final ResultSummary _summary = _result.summary();

        LOG.info("createNode: _id={} propertiesSet={}", _id, _summary.counters().propertiesSet());
        return _id;
    }

    boolean exists(final Transaction _transaction, final String _id)
    {
        LOG.debug("exists: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity) WHERE node._id=$_id RETURN node._id AS id LIMIT 1",
                Collections.singletonMap("_id", _id));
        return _result.list().size() == 1;
    }

    void updateNode(final Transaction _transaction, final String _id, final MSOSEntity _dto)
    {
        LOG.debug("updateNode: _id={}", _id);

        /*
         * Properties
         */
        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();

        // If a property is null, then it was not specified by the caller and we should leave the DB value untouched
        // If Optional.isPresent()==false, then it *was* specified by the caller and set to null explicitly and we
        // should deleteNode the property in the DB.

        final Set<String> _propertiesToDelete = new TreeSet<>();

        // @Searchable @Required String name;
        if (_dto.name != null) {
            if (_dto.name.isPresent()) {
                _params.put("name", _dto.name.get());
            } else {
                throw new RuntimeException("Programming error: name should never be set to Optional.empty()");
            }
        }

        final String UPDATE_NODE_QUERY = Neo4JHelper.getUpdateQuery("MSOSEntity", _params.keySet(),
                                                                    _propertiesToDelete);

        // must have at least one property to set or remove
        if (!_params.isEmpty() && _propertiesToDelete.isEmpty()) {
            // generated ID -- must be put into params *after* generating the UPDATE_NODE_QUERY
            _params.put("_id", _id);

            final StatementResult _result = _transaction.run(UPDATE_NODE_QUERY, _params);
            final ResultSummary _summary = _result.summary();

            LOG.info("updateNode: _id={} propertiesSet={}", _id, _summary.counters().propertiesSet());
        }
    }

    boolean deleteNode(final Transaction _transaction, final String _id)
    {
        LOG.debug("deleteNode: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);
        try {
            final StatementResult _result = _transaction.run("MATCH (node:MSOSEntity {_id:$_id}) DELETE node", _params);
            final ResultSummary _summary = _result.summary();

            LOG.info("deleteNode: _id={} nodesDeleted={}", _id, _summary.counters().nodesDeleted());
            return _summary.counters().nodesDeleted() == 1;
        } catch (ClientException _e) {
            if (_e.code().equals("Neo.ClientError.Schema.ConstraintValidationFailed")) {
                throw new Api409ResourceInUseException(this.linkRegistry.getResourcePath(MSOSEntity.class), _id);
            } else {
                throw _e;
            }
        }
    }

    Optional<String> rel_coreEntity_getid(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_coreEntity_getid: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity {_id:$_id})-[:coreEntity]->(target:CoreEntity) RETURN target._id AS id",
                _params);
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("id");
            final String _ret = _node.asString();

            LOG.debug("rel_coreEntity_getid: _id={} _target_id={}", _id, _ret);
            return Optional.of(_ret);
        } catch (NoSuchRecordException _e) {
            LOG.debug("rel_coreEntity_getid: _id={} _target_id={}", _id, "null");
            return Optional.empty();
        }
    }

    CoreEntity rel_coreEntity_get(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_coreEntity_get: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity {_id:$_id})-[:coreEntity]->(target:CoreEntity) RETURN target",
                _params);
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("target");
            final CoreEntity _ret = CoreEntity.dto(_node);

            LOG.debug("rel_coreEntity_get: _id={} _target_id={}", _id, _ret._id);
            return _ret;
        } catch (NoSuchRecordException _e) {
            throw new Api404MissingRelationshipException(MSOSEntity.class, _id, "coreEntity");
        }
    }

    void rel_coreEntity_add(Transaction _transaction, String _id, Reference<? extends CoreEntity> _reference)
    {
        LOG.debug("rel_coreEntity_add: _id={}, _target_id={}", _id, _reference.getId());

        // create new rel
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        _params.put("_target_id", _reference.getId());

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity {_id:$_id}), (target:CoreEntity {_id:$_target_id}) MERGE (node)" +
                        "-[:coreEntity]->(target) RETURN target._id AS _target_id",
                _params);
        final ResultSummary _summary = _result.summary();
        // Verify
        try {
            // IMPORTANT: keep this, as it will tell if _id or _target_id were incorrect
            // by throwing a NoSuchRecordException
            _result.single();
        } catch (NoSuchRecordException _e) {
            // assumption -- _id is correct
            throw new Api400InvalidReferenceException("coreEntity",
                                                      this.linkRegistry.getResourcePath(CoreEntity.class),
                                                      _reference.getId());
        }

        LOG.info("rel_coreEntity_add: _id={} _target_id={} relationshipsCreated={}",
                 _id,
                 _reference.getId(),
                 _summary.counters().relationshipsCreated());
    }

    void rel_coreEntity_delete(Transaction _transaction, String _id)
    {
        LOG.debug("rel_coreEntity_delete: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:MSOSEntity {_id:$_id})-[rel:coreEntity]->(target:CoreEntity) DELETE rel",
                Collections.singletonMap("_id", _id));
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_coreEntity_delete: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }
}
