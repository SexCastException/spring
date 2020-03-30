package com.huazai.springframework.imports;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author pyh
 * @date 2020/3/28 20:25
 */
public class MyImportSelectors implements ImportSelector {
	@Override
	public String[] selectImports(AnnotationMetadata importingClassMetadata) {
		return new String[]{UserDao.class.getName()};
	}
}
