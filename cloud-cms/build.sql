DROP TABLE IF EXISTS cms_resource_entity;
CREATE TABLE cms_resource_entity (
  id                BIGINT PRIMARY KEY,
  mediaType         VARCHAR(50),
  url               VARCHAR(255) NOT NULL,
  filepath          VARCHAR(255) NOT NULL,
  original_filename VARCHAR(255),
  size              BIGINT       NOT NULL,
  creator_id        BIGINT,
  owner_id          BIGINT,
  owner_role_id     BIGINT,
  create_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
  ON UPDATE CURRENT_TIMESTAMP,
  KEY cms_resource_file_index_media_type (mediaType(6))
);