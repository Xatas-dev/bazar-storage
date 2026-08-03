package org.bazar.bazarstorage.domain.storagenode;

import lombok.Getter;
import lombok.Setter;

/**
 * Ошибка узла хранилища
 */
@Getter
@Setter
public class StorageNodeError {
    /**
     * Код ошибки
     */
    private String errorCode;

    /**
     * Описание ошибки
     */
    private String description;
}
