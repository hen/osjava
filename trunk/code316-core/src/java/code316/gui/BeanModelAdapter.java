package code316.gui;

public class BeanModelAdapter {
	private boolean selected;
	private Object bean;
	public BeanModelAdapter(Object bean) {
		setBean(bean);
	}

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }
}
