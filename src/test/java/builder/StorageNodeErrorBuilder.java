package builder;

import lombok.experimental.UtilityClass;
import org.bazar.bazarstorage.domain.storagenode.StorageNodeError;

@UtilityClass
public class StorageNodeErrorBuilder {
    public static final String DEFAULT_ERROR_CODE = "UNKNOWN";
    public static final String DEFAULT_DESCRIPTION = "description";

    public StorageNodeError buildDefault() {
        StorageNodeError storageNodeError = new StorageNodeError();
        storageNodeError.setCode(DEFAULT_ERROR_CODE);
        storageNodeError.setDescription(DEFAULT_DESCRIPTION);
        return storageNodeError;
    }

    public StorageNodeError buildWith(String code) {
        StorageNodeError storageNodeError = buildDefault();
        storageNodeError.setCode(code);
        return storageNodeError;
    }
}
