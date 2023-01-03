package cg.draw;


import cg.screen.ScreenConverter;
import cg.third.PolyLine3D;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Рисовальщик на графиксе экрана
 */
public abstract class ScreenGraphicsDrawer implements IDrawer {
    private ScreenConverter sc;
    private Graphics2D gr;

    /**
     * Создаёт экземпляр рисовальщика
     * @param sc преобразователь координат
     * @param gr графикс
     */
    public ScreenGraphicsDrawer(ScreenConverter sc, Graphics2D gr) {
        this.sc = sc;
        this.gr = gr;
    }

    public Graphics2D getGraphics() {
        return gr;
    }

    public ScreenConverter getScreenConverter() {
        return sc;
    }
    
    @Override
    public void draw(Collection<PolyLine3D> polylines) {
        List<PolyLine3D> lines = new LinkedList<>();
        IFilter<PolyLine3D> filter = getFilter();
        for (PolyLine3D pl : polylines) {
            if (filter.permit(pl))
                lines.add(pl);
        }
        PolyLine3D[] arr = lines.toArray(new PolyLine3D[0]);
        Arrays.sort(arr, getComparator());
        for (PolyLine3D pl : arr) {
            oneDraw(pl);
        }
    }
    
    @Override
    public void clear(int color) {
        Graphics2D g = getGraphics();
        Color c = g.getColor();
        g.setColor(new Color(color));
        g.fillRect(0, 0, sc.getWs(), sc.getHs());
        g.setColor(c);
    }
    
    /**
     * Метод, умеющий рсовать одну полилинию
     * @param polyline полилиния, которую требуется нарисовать
     */
    protected abstract void oneDraw(PolyLine3D polyline);
    
    /**
     * Должен возвращать фильтр рисуемых полилиний.
     * С помощью него можно оставить только те из них, которые следует рисовать.
     * Например, можно исключить те линии, которые находятся "позади"
     * @return фильтр
     */
    protected abstract IFilter<PolyLine3D> getFilter();
    
    /**
     * Должен возвращать компаратор полилиний для упорядочивания их.
     * @return компаратор
     */
    protected abstract Comparator<PolyLine3D> getComparator();
}
