package br.com.caelum.iogi;

import br.com.caelum.iogi.exceptions.InvalidTypeException;
import br.com.caelum.iogi.parameters.Parameters;
import br.com.caelum.iogi.reflection.ClassConstructor;
import br.com.caelum.iogi.reflection.NewObject;
import br.com.caelum.iogi.reflection.Target;
import br.com.caelum.iogi.spi.DependencyProvider;
import br.com.caelum.iogi.spi.ParameterNamesProvider;
import br.com.caelum.iogi.util.Ints;
import com.google.common.collect.Ordering;

import java.util.Collection;

public class ObjectInstantiator implements Instantiator<Object> {
    private final Ordering<ClassConstructor> orderConstructorsBySize = new Ordering<ClassConstructor>() {
        public int compare(final ClassConstructor first, final ClassConstructor second) {
            return Ints.compare(first.size(), second.size());
        }
    };

    private final Instantiator<Object> argumentInstantiator;
    private final DependenciesInjector dependenciesInjector;
    private final ParameterNamesProvider parameterNamesProvider;

    public ObjectInstantiator(final Instantiator<Object> argumentInstantiator, final DependencyProvider dependencyProvider, final ParameterNamesProvider parameterNamesProvider) {
		this.argumentInstantiator = argumentInstantiator;
		this.dependenciesInjector = new DependenciesInjector(dependencyProvider);
		this.parameterNamesProvider = parameterNamesProvider;
	}

	public boolean isAbleToInstantiate(final Target<?> target) {
		return true;
	}

	public Object instantiate(final Target<?> target, final Parameters parameters) {
		expectingAConcreteTarget(target);
		
		final Parameters parametersForTarget = parameters.focusedOn(target);
        
        NewObject newObject = instantiateWithConstructor(target, parametersForTarget);

        return newObject.withPropertiesSet(parametersForTarget);
	}

    private <T> void expectingAConcreteTarget(final Target<T> target) {
        if (!target.isInstantiable())
            throw new InvalidTypeException("Cannot instantiate abstract type %s", target.getClassType());
    }

    private NewObject instantiateWithConstructor(Target<?> target, Parameters parametersForTarget) {
        final Collection<ClassConstructor> compatibleConstructors = target.compatibleConstructors(parametersForTarget, dependenciesInjector, parameterNamesProvider);

        if (compatibleConstructors.isEmpty()) {
            return NewObject.nullNewObject();
        }

        final ClassConstructor largestMatchingConstructor = orderConstructorsBySize.max(compatibleConstructors);
        return largestMatchingConstructor.instantiate(argumentInstantiator, parametersForTarget, dependenciesInjector);
    }

    
}
