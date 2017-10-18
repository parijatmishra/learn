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
class CoreEntityRepository
{
    private static final Logger LOG = LoggerFactory.getLogger("db.CoreEntity");

    private final IDGenerator idGenerator;
    private final LinkRegistry linkRegistry;

    @Inject
    CoreEntityRepository(final IDGenerator idGenerator, final LinkRegistry linkRegistry)
    {
        this.idGenerator = idGenerator;
        this.linkRegistry = linkRegistry;
    }

    Page<CoreEntity> list(final Transaction _transaction, final String _startId, final int _count)
    {
        LOG.debug("list: startId={} count={}", _startId, _count);

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("startId", _startId);
        // attempt to fetch one more than asked -- this is helpful for pagination
        _params.put("count", _count + 1);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity) WHERE node._id >= $startId RETURN node LIMIT $count",
                _params);
        final List<CoreEntity> _dtos = _result.list(rec -> CoreEntity.dto(rec.get("node")));
        final String _nextStartId = _dtos.size() > _count ? _dtos.remove(_count)._id : null;
        final Page<CoreEntity> _ret = new Page<>(_startId, _count, _dtos, _nextStartId);

        LOG.debug("list: {}", _ret);
        return _ret;
    }

    CoreEntity get(final Transaction _transaction, final String _id)
    {
        LOG.debug("get: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run("MATCH (node:CoreEntity {_id:$_id}) RETURN node", _params);

        try {
            final Record _record = _result.single(); // consumes the result completely
            final Value _node = _record.get("node");
            return CoreEntity.dto(_node);
        } catch (NoSuchRecordException e) {
            throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
        }
    }

    String createNode(final Transaction _transaction, final CoreEntity _dto)
    {
        // generated ID
        final String _id = this.idGenerator.getNextId("CoreEntity");
        LOG.debug("createNode: generatedId={}", _id);

        /*
         * Properties
         */
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        // @Searchable @Required String name;
        _params.put("name", _dto.name.get());
        // @Searchable @Nullable Boolean someProp;
        if (_dto.someProp != null && _dto.someProp.isPresent()) {
            _params.put("someProp", _dto.someProp.get());
        }
        // List<Long> anotherProp;
        // Lists are never stored as null, and are empty by default.
        if (_dto.anotherProp != null && _dto.anotherProp.isPresent()) {
            _params.put("anotherProp", _dto.anotherProp.get());
        } else {
            _params.put("anotherProp", Collections.emptyList());
        }

        final StatementResult _result = _transaction.run("CREATE (node:CoreEntity $params) RETURN node",
                                                         Collections.singletonMap("params", _params));
        final ResultSummary _summary = _result.summary();

        LOG.debug("create: _id={} propertiesSet={}", _id, _summary.counters().propertiesSet());
        return _id;
    }

    boolean exists(final Transaction _transaction, final String _id)
    {
        LOG.debug("exists: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity) WHERE node._id=$_id RETURN node._id AS id LIMIT 1",
                Collections.singletonMap("_id", _id));
        return _result.list().size() == 1;
    }

    void updateNode(final Transaction _transaction, final String _id, final CoreEntity _dto)
    {
        LOG.debug("update: _id={}", _id);

        /*
         * Properties
         */
        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();

        // If a property is null, then it was not specified by the caller and we should leave the DB value untouched
        // If Optional.isPresent()==false, then it *was* specified by the caller and set to null explicitly and we
        // should delete the property in the DB.
        final Set<String> _propertiesToDelete = new TreeSet<>();

        // @Searchable @Required String name;
        if (_dto.name != null) {
            if (_dto.name.isPresent()) {
                _params.put("name", _dto.name.get());
            } else {
                throw new RuntimeException("Programming error: name should never be set to Optional.empty()");
            }
        }
        // @Searchable @Nullable Boolean someProp;
        if (_dto.someProp != null) {
            if (_dto.someProp.isPresent()) {
                _params.put("someProp", _dto.someProp.get());
            } else {
                _propertiesToDelete.add("someProp");
            }
        }
        // List<Long> anotherProp;
        // Lists are never null, and are empty by default.
        if (_dto.anotherProp != null) {
            if (_dto.anotherProp.isPresent()) {
                _params.put("anotherProp", _dto.anotherProp.get());
            } else {
                _params.put("anotherProp", Collections.emptyList());
            }
        }

        final String UPDATE_NODE_QUERY = Neo4JHelper.getUpdateQuery("CoreEntity",
                                                                    _params.keySet(),
                                                                    _propertiesToDelete);

        // must have at least one property to set or remove
        if (!(_params.isEmpty() && _propertiesToDelete.isEmpty())) {
            // generated ID -- must be put into params *after* generating the UPDATE_NODE_QUERY
            _params.put("_id", _id);

            final StatementResult _result = _transaction.run(UPDATE_NODE_QUERY, _params);
            final ResultSummary _summary = _result.summary();

            LOG.info("update: node={} propertiesSet={}", _id, _summary.counters().propertiesSet());
        }
    }

    boolean deleteNode(final Transaction _transaction, final String _id)
    {
        LOG.debug("deleteNode: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        try {
            final StatementResult _result = _transaction.run("MATCH (node:CoreEntity {_id:$_id}) DELETE node", _params);
            final ResultSummary _summary = _result.summary();

            LOG.info("deleteNode: _id={} nodesDeleted={}", _id, _summary.counters().nodesDeleted());
            return _summary.counters().nodesDeleted() == 1;
        } catch (ClientException _e) {
            if (_e.code().equals("Neo.ClientError.Schema.ConstraintValidationFailed")) {
                throw new Api409ResourceInUseException(this.linkRegistry.getResourcePath(CollEntity.class), _id);
            } else {
                throw _e;
            }
        }
    }

    Page<CollEntity> rel_collEntities_list(final Transaction _transaction,
                                           final String _id,
                                           final String _startId,
                                           final int _count)
    {
        LOG.debug("rel_collEntities_list: _id={} _startId={} _count={}", _id, _startId, _count);

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        _params.put("_startId", _startId);
        // attempt to fetch one more than asked -- this is helpful for pagination
        _params.put("_count", _count + 1);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[:collEntities]->(target:CollEntity) WHERE target._id >= " +
                        "$_startId RETURN target LIMIT $_count",
                _params);
        final List<CollEntity> _dtos = _result.list(rec -> CollEntity.dto(rec.get("target")));
        final String _nextStartId = _dtos.size() > _count ? _dtos.remove(_count)._id : null;
        final Page<CollEntity> _ret = new Page<>(_startId, _count, _dtos, _nextStartId);

        LOG.debug("rel_collEntities_list: _id={} {}", _id, _ret);
        return _ret;
    }

    void rel_collEntities_add(final Transaction _transaction,
                              final String _id,
                              final List<Reference<? extends CollEntity>> _addReferences)
    {
        LOG.debug("rel_collEntities_add: _id={} _addReferenceCount={}", _id, _addReferences.size());

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        final List<String> _target_ids = new ArrayList<>(_addReferences.size());
        for (Reference<? extends CollEntity> _reference : _addReferences) {
            _target_ids.add(_reference.getId());
        }
        _params.put("_target_ids", _target_ids);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity), (target:CollEntity) WHERE node._id=$_id AND target._id IN $_target_ids" +
                        "" + " MERGE (node)-[:collEntities]->(target) RETURN target._id AS _target_id",
                _params);
        final ResultSummary _summary = _result.summary();

        // Verify that all rels are present -- if not, find out which ones are not
        final List<String> existingRels = new ArrayList<>(_addReferences.size());
        for (final Record _record : _result.list()) {
            existingRels.add(_record.get("_target_id").asString());
        }
        if (existingRels.size() < _addReferences.size()) {
            for (int _i = 0; _i < _addReferences.size(); _i++) {
                final Reference _ref = _addReferences.get(_i);
                if (!existingRels.contains(_ref.getId())) {
                    _transaction.failure();
                    throw new Api400InvalidReferenceException("[" + _i + "]",
                                                              this.linkRegistry.getResourcePath(CollEntity.class),
                                                              _ref.getId());
                }
            }
        }

        LOG.info("rel_collEntities_add: _id={} relationshipsCreated={}",
                 _id,
                 _summary.counters().relationshipsCreated());

    }

    void rel_collEntities_delete(final Transaction _transaction,
                                 final String _id,
                                 final List<Reference<? extends CollEntity>> _delReferences)
    {
        LOG.debug("rel_collEntities_delete: _id={} _delReferenceCount={}", _id, _delReferences.size());

        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        final List<String> _target_ids = new ArrayList<>(_delReferences.size());
        for (final Reference<? extends CollEntity> _reference : _delReferences) {
            _target_ids.add(_reference.getId());
        }
        _params.put("_target_ids", _target_ids);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity)-[rel:collEntities]->(target:CollEntity) WHERE node._id=$_id AND target._id" + " IN $_target_ids DELETE rel",
                _params);
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_collEntities_delete: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }

    void rel_collEntities_deleteAll(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_collEntities_deleteAll: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[rel:collEntities]->(target:CollEntity) DELETE rel",
                _params);
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_collEntities_deleteAll: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }

    OSEntity rel_osEntity_get(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_osEntity_get: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[:osEntity]->(target:OSEntity) RETURN target",
                _params);
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("target");
            final OSEntity _ret = OSEntity.dto(_node);

            LOG.debug("rel_osEntity_get: _id={} _target_id={}", _id, _ret._id);
            return _ret;
        } catch (NoSuchRecordException _e) {
            throw new Api404MissingRelationshipException(CoreEntity.class, _id, "osEntity");
        }
    }

    void rel_osEntity_add(final Transaction _transaction,
                          final String _id,
                          final Reference<? extends OSEntity> _reference)
    {
        LOG.debug("rel_osEntity_add: _id={} _target_id={}", _id, _reference.getId());

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        _params.put("_target_id", _reference.getId());

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id}), (target:OSEntity {_id:$_target_id}) MERGE (node)-[:osEntity]->"
                        + "(target) RETURN target._id AS _target_id",
                _params);
        final ResultSummary _summary = _result.summary();
        // Verify
        try {
            // IMPORTANT: keep this, as it will tell if _id or _target_id were incorrect
            // by throwing a NoSuchRecordException
            _result.single();
        } catch (NoSuchRecordException _e) {
            // assumption -- _id is correct
            throw new Api400InvalidReferenceException("osEntity",
                                                      this.linkRegistry.getResourcePath(OSEntity.class),
                                                      _reference.getId());
        }

        LOG.info("rel_osEntity_add: _id={} _target_id={} relationshipsCreated={}",
                 _id,
                 _reference.getId(),
                 _summary.counters().relationshipsCreated());
    }

    void rel_osEntity_delete(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_osEntity_delete: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[rel:osEntity]->(target:OSEntity) DELETE rel",
                _params);
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_osEntity_delete: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }

    MSEntity rel_msEntity_get(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_msEntity_get: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[:msEntity]->(target:MSEntity) RETURN target",
                _params);
        final ResultSummary _summary = _result.summary();
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("target");
            final MSEntity _ret = MSEntity.dto(_node);

            LOG.debug("rel_msEntity_get: _id={} _target_id={}", _id, _ret._id);
            return _ret;
        } catch (NoSuchRecordException _e) {
            LOG.debug("rel_msEntity_get: _id={} _target_id={}", _id, "null");
            throw new Api404MissingRelationshipException(CoreEntity.class, _id, "msEntity");
        }
    }

    void rel_msEntity_add(final Transaction _transaction,
                          final String _id,
                          final Reference<? extends MSEntity> _reference)
    {
        LOG.debug("rel_msEntity_add: _id={} _target_id={}", _id, _reference.getId());

        // Parameters to query
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        _params.put("_target_id", _reference.getId());

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id}), (target:MSEntity {_id:$_target_id}) MERGE (node)-[:msEntity]->"
                        + "(target) RETURN target._id AS _target_id",
                _params);
        final ResultSummary _summary = _result.summary();
        // Verify
        try {
            // IMPORTANT: keep this, as it will tell if _id or _target_id were incorrect
            // by throwing a NoSuchRecordException
            _result.single();
        } catch (NoSuchRecordException _e) {
            // assumption -- _id is correct
            throw new Api400InvalidReferenceException("msEntity",
                                                      this.linkRegistry.getResourcePath(MSEntity.class),
                                                      _reference.getId());
        }

        LOG.info("rel_msEntity_add: _id={} _target_id={} relationshipsCreated={}",
                 _id,
                 _reference.getId(),
                 _summary.counters().relationshipsCreated());
    }

    void rel_msEntity_delete(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_msEntity_delete: _id={}", _id);

        // Parameters to query
        final Map<String, Object> _params = Collections.singletonMap("_id", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[rel:msEntity]->(target:MSEntity) DELETE rel",
                _params);
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_msEntity_delete: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }

    Optional<String> rel_msosEntity_getid(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_msosEntity_getid: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[:msosEntity]->(target:MSOSEntity) RETURN target._id AS id",
                Collections.singletonMap("_id", _id));
        final ResultSummary _summary = _result.summary();
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("id");
            final String _ret = _node.asString();
            LOG.debug("rel_msosEntity_getid: _id={} _target_id={}", _id, _ret);

            return Optional.of(_ret);
        } catch (NoSuchRecordException _e) {
            LOG.debug("rel_msosEntity_getid: _id={} _target_id={}", _id, "null");
            return Optional.empty();
        }
    }


    MSOSEntity rel_msosEntity_get(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_msosEntity_get: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[:msosEntity]->(target:MSOSEntity) RETURN target",
                Collections.singletonMap("_id", _id));
        final ResultSummary _summary = _result.summary();
        try {
            final Record _record = _result.single();
            final Value _node = _record.get("target");
            final MSOSEntity _ret = MSOSEntity.dto(_node);

            LOG.debug("rel_msosEntity_get: _id={} _target_id={}", _id, _ret._id);
            return _ret;
        } catch (NoSuchRecordException _e) {
            LOG.debug("rel_msosEntity_get: _id={} _target_id={}", _id, "null");
            throw new Api404MissingRelationshipException(CoreEntity.class, _id, "msosEntity");
        }
    }

    void rel_msosEntity_add(final Transaction _transaction,
                            final String _id,
                            final Reference<? extends MSOSEntity> _reference)
    {
        LOG.debug("rel_msosEntity_add: _id={} _target_id={}", _id, _reference.getId());

        // create new rel
        final Map<String, Object> _params = new TreeMap<>();
        _params.put("_id", _id);
        _params.put("_target_id", _reference.getId());

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id}), (target:MSOSEntity {_id:$_target_id}) MERGE (node)" +
                        "-[:msosEntity]->" + "(target) RETURN target._id AS _target_id",
                _params);
        final ResultSummary _summary = _result.summary();
        // Verify
        try {
            // IMPORTANT: keep this, as it will tell if _id or _target_id were incorrect
            // by throwing a NoSuchRecordException
            _result.single();
        } catch (NoSuchRecordException _e) {
            // assumption -- _id is correct
            throw new Api400InvalidReferenceException("msosEntity",
                                                      this.linkRegistry.getResourcePath(MSOSEntity.class),
                                                      _reference.getId());
        }

        LOG.info("rel_msosEntity_add: _id={} _target_id={} relationshipsCreated={}",
                 _id,
                 _reference.getId(),
                 _summary.counters().relationshipsCreated());
    }

    void rel_msosEntity_delete(final Transaction _transaction, final String _id)
    {
        LOG.debug("rel_msosEntity_delete: _id={}", _id);

        final StatementResult _result = _transaction.run(
                "MATCH (node:CoreEntity {_id:$_id})-[rel:msosEntity]->(target:MSOSEntity) DELETE rel",
                Collections.singletonMap("_id", _id));
        final ResultSummary _summary = _result.summary();

        LOG.info("rel_msosEntity_delete: _id={} relationshipsDeleted={}",
                 _id,
                 _summary.counters().relationshipsDeleted());
    }
}
