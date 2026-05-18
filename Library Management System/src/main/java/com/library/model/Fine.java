package com.library.model;

/**
 * Represents an overdue fine linked to a borrow record.
 */
public class Fine {

    private int     fineId;
    private int     recordId;
    private double  amount;
    private boolean paid;

    // ── Constructors ─────────────────────────────────────────────────────────
    public Fine() {}

    public Fine(int fineId, int recordId, double amount, boolean paid) {
        this.fineId   = fineId;
        this.recordId = recordId;
        this.amount   = amount;
        this.paid     = paid;
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public int     getFineId()         { return fineId; }
    public void    setFineId(int v)    { this.fineId = v; }

    public int     getRecordId()       { return recordId; }
    public void    setRecordId(int v)  { this.recordId = v; }

    public double  getAmount()         { return amount; }
    public void    setAmount(double v) { this.amount = v; }

    public boolean isPaid()            { return paid; }
    public void    setPaid(boolean v)  { this.paid = v; }
}
