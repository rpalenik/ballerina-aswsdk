package org.ballerinalang.crypto.generated.providers;

import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.NativeElementRepository;
import org.ballerinalang.natives.NativeElementRepository.NativeActionDef;
import org.ballerinalang.natives.NativeElementRepository.NativeFunctionDef;
import org.ballerinalang.spi.NativeElementProvider;

@JavaSPIService ("org.ballerinalang.spi.NativeElementProvider")
public class StandardNativeElementProvider implements NativeElementProvider {

	@Override
	public void populateNatives(NativeElementRepository repo) {
		repo.registerNativeFunction(new NativeFunctionDef("in2", "crypto:0.0.0", "firehoseBatchPut", new TypeKind[] { TypeKind.STRING, TypeKind.STRING }, new TypeKind[] { TypeKind.STRING }, "hr.in2.ballerina.crypto.FirehoseBatchPut"));
		repo.registerNativeFunction(new NativeFunctionDef("in2", "crypto:0.0.0", "hmac", new TypeKind[] { TypeKind.STRING, TypeKind.STRING, TypeKind.STRING, TypeKind.STRING }, new TypeKind[] { TypeKind.STRING }, "hr.in2.ballerina.crypto.Hmac"));
	}

}
