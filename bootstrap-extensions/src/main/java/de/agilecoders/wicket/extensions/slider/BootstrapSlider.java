package de.agilecoders.wicket.extensions.slider;

import de.agilecoders.wicket.extensions.slider.res.BootstrapSliderCssResourceReference;
import de.agilecoders.wicket.extensions.slider.res.BootstrapSliderResourceReference;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * BootstrapSlider: a Wicket wrapper for: https://github.com/seiyria/bootstrap-slider
 *
 * @author Ernesto Reinaldo Barreiro (reiern70@gmail.com)
 */
public class BootstrapSlider<T extends ISliderValue, N extends Number> extends TextField<T> {

    private static Logger logger = LoggerFactory.getLogger(BootstrapSlider.class);

    public enum TooltipType {
        show, hide, always
    }

    public enum HandleType {
        round, square, triangle, custom
    }

    public enum Orientation {
        horizontal, vertical
    }

    public enum Scale {
        logarithmic,
        linear
    }

    private final IConverter<ISliderValue> converter;

    private N min;
    private N max;
    private N step;
    private TooltipType tooltip;
    private HandleType handle;
    // function to create custom tooltip.
    private String formatter;
    private Orientation orientation;
    /**
     * whether or not the slider should be reversed
     */
    private Boolean reversed;
    /**
     * if false show one tooltip if true show two tooltips one for each handler
     */
    private Boolean tooltipSplit;
    /**
     * The number of digits shown after the decimal. Defaults to the number of digits after the decimal of step value.
     */
    private Integer precision;

    private Scale scale;

    public BootstrapSlider(final String id, final IModel<T> model, final Class<T> typeClass)
    {
        super(id, model, typeClass);
        setOutputMarkupId(true);
        try {
            if(Double.class.isAssignableFrom(typeClass.newInstance().getNumberType())) {
                converter = new BootstrapDoubleSliderConverter();
            } else if(Integer.class.isAssignableFrom(typeClass.newInstance().getNumberType())) {
                converter = new BootstrapIntegerSliderConverter();
            } else {
                converter = new BootstrapLongSliderConverter();
            }
        } catch (InstantiationException | IllegalAccessException e) {
            throw new WicketRuntimeException(e);
        }
    }

    protected T newInstance() {
        try {
            return getType().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("An error occurred while instantiating ISliderValue", e);
        }
        return null;
    }

    @Override
    public String getInput() {
        String input = super.getInput();
        if (input != null && input.indexOf(',') > 0) {
            return "[" + input + "]";
        }
        return input;
    }

    @Override
    public <C> IConverter<C> getConverter(final Class<C> type)
    {
        if (ISliderValue.class.isAssignableFrom(type))
        {
            return (IConverter<C>)converter;
        }
        else
        {
            return super.getConverter(type);
        }
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);
        // Must be attached to an input tag
        checkComponentTag(tag, "input");
        tag.put("data-slider-value", converter.convertToString(getModelObject(), getLocale()));
        tag.put("data-slider-min", min != null ? min.toString() : "0");
        tag.put("data-slider-max",max != null? max.toString(): "10");
        tag.put("data-slider-step",step != null? step.toString(): "1");
        tag.put("type", "text");
        if(tooltip != null) {
            tag.put("data-slider-tooltip", tooltip.name());
        }
        if(handle != null) {
            tag.put("data-slider-handle", handle.name());
        }
        if(!isEnabled()) {
            tag.put("data-slider-enabled", false);
        }
        if(orientation != null) {
            tag.put("data-slider-orientation", orientation.name());
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(BootstrapSliderResourceReference.getInstance()));
        response.render(CssHeaderItem.forReference(BootstrapSliderCssResourceReference.getInstance()));
        StringBuilder builder = new StringBuilder();
        builder.append("$('#").append(getMarkupId()).append("').slider({");
        if(formatter != null) {
            builder.append("'formatter':").append(formatter);
        }
        if(reversed != null && reversed.equals(true)) {
            builder.append("'reversed':").append(reversed);
        }
        if(tooltipSplit != null && tooltipSplit.equals(true)) {
            builder.append("'tooltip_split':").append(tooltipSplit);
        }
        if(precision != null) {
            builder.append("'precision':").append(precision);
        }
        if(scale != null) {
            builder.append("'scale':").append(scale);
        }
        configExtraParams(builder);
        builder.append("})");
        configEvents(builder);
        builder.append(";");
        response.render(OnDomReadyHeaderItem.forScript(builder));
    }

    protected void configExtraParams(final StringBuilder builder) {
    }

    protected void configEvents(StringBuilder builder) {
    }

    public N getMin() {
        return min;
    }

    public BootstrapSlider setMin(N min) {
        this.min = min;
        return this;
    }

    public N getMax() {
        return max;
    }

    public BootstrapSlider setMax(N max) {
        this.max = max;
        return this;
    }

    public N getStep() {
        return step;
    }

    public BootstrapSlider setStep(N step) {
        this.step = step;
        return this;
    }

    public TooltipType getTooltip() {
        return tooltip;
    }

    public BootstrapSlider setTooltip(TooltipType tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public HandleType getHandle() {
        return handle;
    }

    public BootstrapSlider setHandle(HandleType handle) {
        this.handle = handle;
        return this;
    }

    public String getFormatter() {
        return formatter;
    }

    public BootstrapSlider setFormatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public BootstrapSlider setOrientation(Orientation orientation) {
        this.orientation = orientation;
        return this;
    }

    public Boolean getReversed() {
        return reversed;
    }

    public BootstrapSlider setReversed(Boolean reversed) {
        this.reversed = reversed;
        return this;
    }

    public Boolean getTooltipSplit() {
        return tooltipSplit;
    }

    public BootstrapSlider setTooltipSplit(Boolean tooltipSplit) {
        this.tooltipSplit = tooltipSplit;
        return this;
    }

    public Integer getPrecision() {
        return precision;
    }

    public BootstrapSlider setPrecision(Integer precision) {
        this.precision = precision;
        if(precision != null && precision < 0) {
            throw new IllegalArgumentException("Precision should be a positive number");
        }
        return this;
    }

    public Scale getScale() {
        return scale;
    }

    public BootstrapSlider setScale(Scale scale) {
        this.scale = scale;
        return this;
    }
}
