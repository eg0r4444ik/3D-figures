package cg.draw;


import cg.third.PolyLine3D;

/**
 * Интерфейс декларирует метод, который будет отвечать, подходит ли заданный экземпляр класса какому-либо условию
 */
public interface IFilter<T> {
    boolean permit(T value);

    boolean accept(PolyLine3D line);
}
