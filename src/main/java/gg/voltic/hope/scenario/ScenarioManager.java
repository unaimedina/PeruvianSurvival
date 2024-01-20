package gg.voltic.hope.scenario;

import gg.voltic.hope.Hope;
import lombok.Getter;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ScenarioManager {
    private final List<Scenario> scenarios = new ArrayList<>();
    private final Map<String, Scenario> scenariosByName = new HashMap<>();

    public ScenarioManager() {
        Reflections reflections = new Reflections("gg.voltic.hope.scenario.scenarios");
        List<Class<? extends Scenario>> classes = new ArrayList<>(reflections.getSubTypesOf(Scenario.class));
        classes.forEach(clazz -> {
            try {
                Scenario scenario = clazz.getConstructor().newInstance();
                this.scenarios.add(scenario);
                if (!scenario.isEnabled() && Hope.getInstance().getModulesFile().getConfig().getList("MODULES").contains(scenario.getName()) && !Hope.getInstance().getModulesFile().getConfig().getList("MODULES").isEmpty()) {
                    scenario.enable();
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                     InstantiationException var3) {
                var3.printStackTrace();
            }
        });
        this.scenarios.forEach(scenario -> this.scenariosByName.put(scenario.getName(), scenario));
    }

    public Scenario getScenario(String name) {
        return this.scenariosByName.get(name);
    }

}
