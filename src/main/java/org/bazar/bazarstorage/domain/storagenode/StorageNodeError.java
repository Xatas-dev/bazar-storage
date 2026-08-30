package org.bazar.bazarstorage.domain.storagenode;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ошибка узла хранилища
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StorageNodeError {
    /**
     * Код ошибки
     */
    private String code;

    /**
     * Описание ошибки
     */
    private String description;
}
