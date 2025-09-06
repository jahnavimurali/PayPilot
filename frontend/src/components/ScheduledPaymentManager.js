import React, { useState, useEffect, useMemo } from "react";
import axios from "axios";
import { ConfirmToast } from "./ConfirmToast";
import { toast } from "react-toastify"; 

const ScheduledPaymentManager = () => {
    const user = JSON.parse(localStorage.getItem("user"));
    const [bills, setBills] = useState([]);
    const [scheduledPayments, setScheduledPayments] = useState([]);
    const [paymentMethods, setPaymentMethods] = useState([]);
    const [formData, setFormData] = useState({
        id: null,
        billId: "",
        amount: "",
        paymentMethod: "UPI",
        scheduledDate: new Date().toISOString().split("T")[0],
    });
    const [viewType, setViewType] = useState("all");

    useEffect(() => {
        fetchBills();
        fetchPaymentMethods();
    }, []);

    useEffect(() => {
        fetchScheduledPayments();
    }, [viewType]);

    const fetchBills = async () => {
        try {
            const res = await axios.get(`http://localhost:9090/api/bills/${user.id}`);
            setBills(res.data || []);
        } catch (err) {
            console.error("Error fetching bills:", err);
        }
    };

    const fetchPaymentMethods = async () => {
        try {
            const res = await axios.get("http://localhost:9090/api/payment_methods");
            setPaymentMethods((res.data || []).filter((m) => m !== "CASH"));
        } catch (err) {
            console.error("Error fetching payment methods:", err);
        }
    };

    const fetchScheduledPayments = async () => {
        try {
            let res;
            if (viewType === "all") {
                res = await axios.get(
                    `http://localhost:9090/api/scheduled-payments/user/${user.id}`
                );
            } else if (viewType === "upcoming") {
                res = await axios.get(
                    `http://localhost:9090/api/scheduled-payments/upcoming/${user.id}`
                );
            } else {
                res = await axios.get(
                    `http://localhost:9090/api/scheduled-payments/history/${user.id}`
                );
            }
            setScheduledPayments(res.data || []);
        } catch (err) {
            console.error("Error fetching scheduled payments:", err);
        }
    };

    // Bills that are NOT fully paid (no paid scheduled payment for that bill)
    const unpaidBills = useMemo(() => {
        const paidSet = new Set(
            (scheduledPayments || [])
                .filter((sp) => sp.isPaid)
                .map((sp) => sp.billId)
        );
        return (bills || []).filter((b) => !paidSet.has(b.id));
    }, [bills, scheduledPayments]);

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // Auto-fill amount/method/date on bill select
    const handleBillSelect = (billId) => {
        const selected = unpaidBills.find((b) => b.id === parseInt(billId, 10));
        if (!selected) return;
        setFormData((prev) => ({
            ...prev,
            billId,
            amount: selected.amount ?? "",
            paymentMethod: "UPI",
            scheduledDate: selected.dueDate || new Date().toISOString().split("T")[0],
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (formData.id) {
                await axios.put(
                    `http://localhost:9090/api/scheduled-payments/${formData.id}`,
                    { ...formData, userId: user.id }
                );
                toast.success("Payment updated successfully!");
            } else {
                await axios.post("http://localhost:9090/api/scheduled-payments", {
                    ...formData,
                    userId: user.id,
                });
                toast.success("Payment scheduled!");
            }
            await fetchScheduledPayments();
            resetForm();
        } catch (err) {
            console.error("Error saving payment:", err);
        }
    };

    const handleEdit = (payment) => {
        setFormData({
            id: payment.id,
            billId: payment.billId,
            amount: payment.amount,
            paymentMethod: payment.paymentMethod,
            scheduledDate: payment.scheduledDate,
        });
    };

    const handleDelete = async (id) => {
        ConfirmToast(
        "Are you sure you want to delete this item?",
        async() => {
        try {
            await axios.delete(`http://localhost:9090/api/scheduled-payments/${id}`);
            await fetchScheduledPayments();
            console.log("Item deleted");
            toast.success("Item deleted!");
        } catch (err) {
            console.error("Error deleting payment:", err);
            toast.error("Delete failed");
        }
        },
        () => {
        console.log("Delete canceled");
        toast.info("Action canceled");
        }
    );
    };

    const handleMarkAsPaid = async (id) => {
        ConfirmToast(
        "Are you sure you want to mark this payment as paid?",
        async() => {
        try {
            await axios.put(
                `http://localhost:9090/api/scheduled-payments/markPaid/${id}`
            );
            await fetchScheduledPayments();
            console.log("Marked as paid");
            toast.success("Marked as paid!");
        } catch (err) {
            console.error("Error marking payment as paid:", err);
            toast.error("Mark as paid failed");
        }
        },
        () => {
            console.log("Marked as paid canceled");
            toast.info("Action canceled");
        }
    );
    };

    const handlePay = async (id) => {
        ConfirmToast(
        "Are you sure you want to pay this bill now?",
        async() => {
        try {
            await axios.put(
                `http://localhost:9090/api/scheduled-payments/markPaid/${id}`
            );
            await fetchScheduledPayments();
            console.log("Bill paid");
            toast.success("Bill paid!");
        } catch (err) {
            console.error("Error in making payment:", err);
            toast.error("Bill payment failed");
        }
        },
        () => {
            console.log("Paid canceled");
            toast.info("Action canceled");
        }
    );
    };

    const resetForm = () => {
        setFormData({
            id: null,
            billId: "",
            amount: "",
            paymentMethod: "UPI",
            scheduledDate: new Date().toISOString().split("T")[0],
        });
    };

    return (
        <div className="container mt-4">
            <h3>💳 {formData.id ? "Edit Payment" : "Schedule a Payment"}</h3>

            <form className="row g-3 align-items-end" onSubmit={handleSubmit}>
                <div className="col-md-3">
                    <label className="form-label">Bill</label>
                    <select
                        className="form-select"
                        name="billId"
                        value={formData.billId}
                        onChange={(e) => handleBillSelect(e.target.value)}
                        required
                    >
                        <option value="">Select Unpaid Bill</option>
                        {unpaidBills.map((b) => (
                            <option key={b.id} value={b.id}>
                                {b.title} (₹{b.amount})
                            </option>
                        ))}
                    </select>
                </div>

                <div className="col-md-2">
                    <label className="form-label">Amount</label>
                    <input
                        type="number"
                        name="amount"
                        className="form-control"
                        value={formData.amount}
                        onChange={handleChange}
                        readOnly
                    />
                </div>

                <div className="col-md-3">
                    <label className="form-label">Payment Method</label>
                    <select
                        className="form-select"
                        name="paymentMethod"
                        value={formData.paymentMethod}
                        onChange={handleChange}
                        required
                    >
                        {paymentMethods.map((method) => (
                            <option key={method} value={method}>
                                {method}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="col-md-2">
                    <label className="form-label">Date</label>
                    <input
                        type="date"
                        name="scheduledDate"
                        className="form-control"
                        value={formData.scheduledDate}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div className="col-md-2 d-flex">
                    <button type="submit" className="btn btn-primary w-100 me-2">
                        {formData.id ? "Update" : "Schedule"}
                    </button>
                    {formData.id && (
                        <button
                            type="button"
                            className="btn btn-secondary w-100"
                            onClick={resetForm}
                        >
                            Cancel
                        </button>
                    )}
                </div>
            </form>

            <hr className="my-4" />

            <div className="btn-group mb-3">
                <button
                    className={`btn ${viewType === "all" ? "btn-secondary" : "btn-outline-secondary"}`}
                    onClick={() => setViewType("all")}
                >
                    All
                </button>
                <button
                    className={`btn ${viewType === "upcoming" ? "btn-info" : "btn-outline-info"}`}
                    onClick={() => setViewType("upcoming")}
                >
                    Upcoming
                </button>
                <button
                    className={`btn ${viewType === "history" ? "btn-warning" : "btn-outline-warning"}`}
                    onClick={() => setViewType("history")}
                >
                    History
                </button>
            </div>

            <table className="table table-bordered">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Bill</th>
                    <th>Amount</th>
                    <th>Method</th>
                    <th>Date</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {scheduledPayments.map((sp) => {
                    const billTitle = bills.find((b) => b.id === sp.billId)?.title || sp.billId;
                    return (
                        <tr key={sp.id}>
                            <td>{sp.id}</td>
                            <td>{billTitle}</td>
                            <td>₹{sp.amount}</td>
                            <td>{sp.paymentMethod}</td>
                            <td>{sp.scheduledDate}</td>
                            <td>
                                {!sp.isPaid && (
                                    <>
                                        <button
                                            className="btn btn-sm btn-info me-2"
                                            onClick={() => handleEdit(sp)}
                                        >
                                            Edit
                                        </button>
                                        <button
                                            className="btn btn-sm btn-success me-2"
                                            onClick={() => handlePay(sp.id)}
                                        >
                                            Pay
                                        </button>
                                        <button
                                            className="btn btn-sm btn-outline-success"
                                            onClick={() => handleMarkAsPaid(sp.id)}
                                        >
                                            Mark as Paid
                                        </button>
                                    </>
                                )}
                                {sp.isPaid && (
                                    <button className="btn btn-sm btn-success" disabled>
                                        ✅ Paid
                                    </button>
                                )}
                                <button
                                    className="btn btn-sm btn-danger ms-2"
                                    onClick={() => handleDelete(sp.id)}
                                >
                                    Delete
                                </button>
                            </td>
                        </tr>
                    );
                })}
                </tbody>
            </table>
        </div>
    );
};

export default ScheduledPaymentManager;