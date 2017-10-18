package demo.domain.generated;

import lib.db.Page;
import lib.db.Reference;
import lib.exc.Api400InconsistentRequestException;
import lib.exc.Api400InvalidReferenceException;
import lib.exc.Api404MissingResourceException;
import lib.exc.Api409ResourceInUseException;
import lib.neo4j.Neo4JHelper;
import lib.web.Constants;
import lib.web.LinkRegistry;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

/**
 * DB operations. Blocking.
 */
class CoreEntityService
{
    private static final Logger LOG = LoggerFactory.getLogger("svc.CoreEntity");

    private final Driver driver;
    private final LinkRegistry linkRegistry;
    private final CoreEntityRepository coreEntityRepository;
    private final MSOSEntityRepository msosEntityRepository;

    @Inject
    public CoreEntityService(final Driver driver,
                             final LinkRegistry linkRegistry,
                             final CoreEntityRepository coreEntityRepository,
                             final MSOSEntityRepository msosEntityRepository)
    {
        this.driver = driver;
        this.linkRegistry = linkRegistry;
        this.coreEntityRepository = coreEntityRepository;
        this.msosEntityRepository = msosEntityRepository;
    }

    public void init()
    {
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // ID is unique
                Neo4JHelper.createUniqueConstraint(_transaction, "CoreEntity", "_id", LOG);

                // @Searchable @Required String name;
                Neo4JHelper.createIndex(_transaction, "CoreEntity", "name", LOG);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    Page<CoreEntity> list(final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final Page<CoreEntity> _ret = coreEntityRepository.list(_transaction, _startId, _count);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CoreEntity get(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                CoreEntity _ret = coreEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CoreEntity create(final CoreEntity _dto)
    {
        // Database operation
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                final String _id = coreEntityRepository.createNode(_transaction, _dto);

                /*
                 * Uni-directional Relationships
                 */
                // @Relationship List<Reference<CollEntity>> collEntities;
                if (_dto.collEntities != null && _dto.collEntities.isPresent()) {
                    this.coreEntityRepository.rel_collEntities_add(_transaction, _id, _dto.collEntities.get());
                }

                // @Relationship(required=false) Optional<Reference<? extends OSEntity>> osEntity;
                if (_dto.osEntity != null && _dto.osEntity.isPresent()) {
                    this.coreEntityRepository.rel_osEntity_add(_transaction, _id, _dto.osEntity.get());
                }

                // @Relationship(required=true) Optional<Reference<? extends MSEntity>> msEntity;
                this.coreEntityRepository.rel_msEntity_add(_transaction, _id, _dto.msEntity.get());

                /*
                 * Bi-directional Relationships
                 */
                // @Relationship(required=true, opposite="coreEntity") Optional<Reference<? extends MSOSEntity>
                // msosEntity;
                rel_msosEntity_set(_transaction, _id, _dto.msosEntity.get());

                final CoreEntity _ret = this.coreEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    CoreEntity update(final String _id, final CoreEntity _dto)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }
                this.coreEntityRepository.updateNode(_transaction, _id, _dto);

                /*
                 * Uni-directional Relationships
                 */
                // @Relationship List<Reference<CollEntity>> collEntities;
                if (_dto.collEntities != null) {
                    this.coreEntityRepository.rel_collEntities_deleteAll(_transaction, _id);
                    if (_dto.collEntities.isPresent()) {
                        this.coreEntityRepository.rel_collEntities_add(_transaction, _id, _dto.collEntities.get());
                    }
                }

                // @Relationship(required=false) Optional<Reference<? extends OSEntity>> osEntity;
                if (_dto.osEntity != null) {
                    this.coreEntityRepository.rel_osEntity_delete(_transaction, _id);
                    if (_dto.osEntity.isPresent()) {
                        this.coreEntityRepository.rel_osEntity_add(_transaction, _id, _dto.osEntity.get());
                    }
                }

                // @Relationship(required=true) Optional<Reference<? extends MSEntity>> msEntity;
                if (_dto.msEntity != null && _dto.msEntity.isPresent()) {
                    this.coreEntityRepository.rel_msEntity_delete(_transaction, _id);
                    this.coreEntityRepository.rel_msEntity_add(_transaction, _id, _dto.msEntity.get());
                }

                /*
                 * Bi-directional relationships
                 */
                // @Relationship(required=true, opposite="coreEntity") Optional<Reference<? extends MSOSEntity>
                // msosEntity;
                if (_dto.msosEntity != null && _dto.msosEntity.isPresent()) {
                    rel_msosEntity_set(_transaction, _id, _dto.msosEntity.get());
                }

                final CoreEntity _ret = this.coreEntityRepository.get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    boolean delete(final String _id)
    {
        // Database query
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }
                /*
                 * Delete outgoing uni-directional relationships
                 */
                this.coreEntityRepository.rel_collEntities_deleteAll(_transaction, _id);
                this.coreEntityRepository.rel_osEntity_delete(_transaction, _id);
                this.coreEntityRepository.rel_msEntity_delete(_transaction, _id);
                /*
                 * Delete bi-directional relationship where the incoming relatinoship is not mandatory
                 */
                this.coreEntityRepository.rel_msosEntity_delete(_transaction, _id);
                // relDelete_msosEntity_incoming_txn(_transaction, _id);

                final boolean _ret = this.coreEntityRepository.deleteNode(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            } catch (ClientException _e) {
                if (_e.code().equals("Neo.ClientError.Schema.ConstraintValidationFailed")) {
                    throw new Api409ResourceInUseException(this.linkRegistry.getResourcePath(OSEntity.class), _id);
                } else {
                    throw _e;
                }
            }
        }
    }

    Page<CollEntity> rel_collEntities_list(final String _id, final String _startId, final int _count)
    {
        // Database operations
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                final Page<CollEntity> _page = this.coreEntityRepository.rel_collEntities_list(_transaction,
                                                                                               _id,
                                                                                               _startId,
                                                                                               _count);

                _transaction.success();
                _transaction.close();

                return _page;
            }
        }
    }

    Page<CollEntity> rel_collEntities_add(final String _id, final List<Reference<? extends CollEntity>> _addReferences)
    {
        // Database operations
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                this.coreEntityRepository.rel_collEntities_add(_transaction, _id, _addReferences);

                final Page<CollEntity> _page = this.coreEntityRepository.rel_collEntities_list(_transaction,
                                                                                               _id,
                                                                                               "0",
                                                                                               Constants
                                                                                                       .PARAM_LIST_COUNT_DEFAULT);

                _transaction.success();
                _transaction.close();

                return _page;
            }
        }
    }

    Page<CollEntity> rel_collEntities_patch(final String _id,
                                            final List<Reference<? extends CollEntity>> _addReferences,
                                            final List<Reference<? extends CollEntity>> _delReferences)
    {
        // Database operations
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                // ensure no ref common between deletes and adds -- otherwise the result
                // will depend on the order of calling delete and add, and we don't want
                // the caller to depend on our implementation order
                if (_addReferences.removeAll(_delReferences) == true) { // some elements are common
                    throw new Api400InconsistentRequestException(
                            "Some reference(s) specified in BOTH `add` and `delete`");
                }

                this.coreEntityRepository.rel_collEntities_delete(_transaction, _id, _delReferences);
                this.coreEntityRepository.rel_collEntities_add(_transaction, _id, _addReferences);

                final Page<CollEntity> _page = this.coreEntityRepository.rel_collEntities_list(_transaction,
                                                                                               _id,
                                                                                               "0",
                                                                                               Constants
                                                                                                       .PARAM_LIST_COUNT_DEFAULT);

                _transaction.success();
                _transaction.close();

                return _page;
            }
        }
    }

    void rel_collEntities_deleteAll(final String _id)
    {
        // Database operations
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                this.coreEntityRepository.rel_collEntities_deleteAll(_transaction, _id);

                _transaction.success();
                _transaction.close();
            }
        }

    }

    OSEntity rel_osEntity_get(final String _id)
    {
        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                final OSEntity _ret = this.coreEntityRepository.rel_osEntity_get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    void rel_osEntity_set(final String _id, final Reference<? extends OSEntity> _reference)
    {
        LOG.debug("rel_osEntity_set: _id={} _target_id={}", _id, _reference.getId());

        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                this.coreEntityRepository.rel_osEntity_delete(_transaction, _id);
                this.coreEntityRepository.rel_osEntity_add(_transaction, _id, _reference);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    void rel_osEntity_delete(final String _id)
    {
        LOG.debug("rel_osEntity_delete: _id={}", _id);

        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                this.coreEntityRepository.rel_osEntity_delete(_transaction, _id);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    MSEntity rel_msEntity_get(final String _id)
    {
        LOG.debug("rel_msEntity_get: _id={}", _id);

        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                final MSEntity _ret = this.coreEntityRepository.rel_msEntity_get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    void rel_msEntity_set(final String _id, final Reference<? extends MSEntity> _reference)
    {
        LOG.debug("rel_msEntity_set: _id={} _target_id={}", _id, _reference.getId());

        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                this.coreEntityRepository.rel_msEntity_delete(_transaction, _id);
                this.coreEntityRepository.rel_msEntity_add(_transaction, _id, _reference);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    MSOSEntity rel_msosEntity_get(final String _id)
    {
        LOG.debug("rel_msosEntity_get: _id={}", _id);

        try (Session _session = this.driver.session(AccessMode.READ)) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                final MSOSEntity _ret = this.coreEntityRepository.rel_msosEntity_get(_transaction, _id);

                _transaction.success();
                _transaction.close();

                return _ret;
            }
        }
    }

    void rel_msosEntity_set(final String _id, final Reference<? extends MSOSEntity> _reference)
    {
        try (Session _session = this.driver.session()) {
            try (Transaction _transaction = _session.beginTransaction()) {
                // Ensure entity exists
                if (!this.coreEntityRepository.exists(_transaction, _id)) {
                    throw new Api404MissingResourceException(this.linkRegistry.getResourcePath(CoreEntity.class), _id);
                }

                rel_msosEntity_set(_transaction, _id, _reference);

                _transaction.success();
                _transaction.close();
            }
        }
    }

    private void rel_msosEntity_set(final Transaction _transaction,
                                    final String _id,
                                    final Reference<? extends MSOSEntity> _reference)
    {
        LOG.debug("rel_msosEntity_set: _id={} _target_id={}", _id, _reference.getId());

        // Target exists
        if (!this.msosEntityRepository.exists(_transaction, _reference.getId())) {
            throw new Api400InvalidReferenceException("msosEntity",
                                                      this.linkRegistry.getResourcePath(MSOSEntity.class),
                                                      _reference.getId());
        }

        // Target is not already used up
        final Optional<String> _other_id = this.msosEntityRepository.rel_coreEntity_getid(_transaction,
                                                                                          _reference.getId());

        if (_other_id.isPresent()) {
            LOG.debug("rel_msosEntity_set: _target_id={} connected to CoreEntity{_id={}}; cannot change ref",
                      _reference.getId(),
                      _other_id.get());
            throw new Api409ResourceInUseException(this.linkRegistry.getResourcePath(MSOSEntity.class),
                                                   _reference.getId());

        }

        // Do we already have a rel? Delete it
        final Optional<String> msosEntity_id = this.coreEntityRepository.rel_msosEntity_getid(_transaction, _id);
        if (msosEntity_id.isPresent()) {
            this.coreEntityRepository.rel_msosEntity_delete(_transaction, _id);
            this.msosEntityRepository.rel_coreEntity_delete(_transaction, msosEntity_id.get());
        }

        // Add new rel
        this.coreEntityRepository.rel_msosEntity_add(_transaction, _id, _reference);
        this.msosEntityRepository.rel_coreEntity_add(_transaction,
                                                     _reference.getId(),
                                                     new Reference<>(CoreEntity.class, _id));
    }
}
