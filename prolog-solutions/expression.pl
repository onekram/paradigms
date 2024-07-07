:- load_library('alice.tuprolog.lib.DCGLibrary').

to_lower(C, LC) :-
    char_code(C, CC),
    (CC >= 65, CC =< 90 ->
        LCC is CC + 32,
        char_code(LC, LCC);
    LC = C).

lookup(K, [(K1, V) | _], V) :- atom_chars(K, [H | _]), to_lower(H, K1).
lookup(K, [_ | T], V) :- lookup(K, T, V).

ws --> [].
ws --> [' '], ws.

check_p([], L) --> [].
check_p([H | T], L) -->
  { member(H, L) },
  [H], check_p(T, L).

unop_p(op_negate) --> [n, e, g, a, t, e].
unop_p(op_bitnot) --> ['~'].

binop_p(op_add)    --> ['+'].
binop_p(op_subtract) --> ['-'].
binop_p(op_multiply) --> ['*'].
binop_p(op_divide)  --> ['/'].
binop_p(op_bitand)  --> ['&'].
binop_p(op_bitor)   --> ['|'].
binop_p(op_bitxor)  --> ['^'].

ternop_p(op_bitif) --> ['?'].
ternop_p(op_bitmux) --> ['¿'].

expr_p(variable(N)) -->
 { nonvar(N) -> call(atom_chars(N, CS)); var(N) },
 ws, check_p(CS, ['X', 'Y', 'Z', x, y, z]), ws,
 {CS = [_ | _], atom_chars(N, CS)}.


expr_p(const(V)) -->
  { nonvar(V) -> call(number_chars(V, CS)); var(V) },
  ws, check_p(CS, ['.', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-']), ws,
  { CS = [_ | _], CS \= ['-'], number_chars(V, CS) }.

expr_p(operation(OP, A)) -->
    ws, unop_p(OP),
    [' '], ws,
    expr_p(A), ws.

expr_p(operation(OP, A, B)) -->
    ws, ['('],
    ws, expr_p(A), [' '],
    ws, binop_p(OP), ws,
    [' '], expr_p(B),
    ws, [')'], ws.

expr_p(operation(OP, A, B, C)) -->
    ws, ['('],
    ws, expr_p(A), [' '],
    ws, ternop_p(OP), ws,
    [' '], expr_p(B),
    ws, [' '], [':'], [' '], ws,
    expr_p(C), ws,
    [')'], ws.

infix_str(E, A) :- ground(E),
    phrase(expr_p(E), C), atom_chars(A, C), !.

infix_str(E, A) :- atom(A),
    atom_chars(A, C), phrase(expr_p(E), C), !.

evaluate(const(V), _, V).
evaluate(variable(N), VS, V) :-
    lookup(N, VS, V).

evaluate(operation(OP, A), VS, R) :-
    evaluate(A, VS, RA),
    op_impl(OP, RA, R).

evaluate(operation(OP, A, B), VS, R) :-
    evaluate(A, VS, RA),
    evaluate(B, VS, RB),
    op_impl(OP, RA, RB, R).

evaluate(operation(OP, A, B, C), VS, R) :-
    evaluate(A, VS, RA),
    evaluate(B, VS, RB),
    evaluate(C, VS, RC),
    op_impl(OP, RA, RB, RC, R).

op_impl(op_negate, A, R) :- R is -A.
op_impl(op_bitnot, A, R) :- R is \A.

op_impl(op_add, A, B, R) :- R is A + B.
op_impl(op_subtract, A, B, R) :- R is A - B.
op_impl(op_multiply, A, B, R) :- R is A * B.
op_impl(op_divide, A, B, R) :- B =:= 0 -> R = 0; R is A / B.
op_impl(op_bitand, A, B, R) :- R is A /\ B.
op_impl(op_bitor, A, B, R) :- R is A \/ B.
op_impl(op_bitxor, A, B, R) :- R is (A \/ B) /\ \(A /\ B).

op_impl(op_bitif, A, B, C, R) :- A /\ 1 =:= 1 -> R = B; R = C.
op_impl(op_bitmux, A, B, C, R) :- R is (\A /\ B) \/ (A /\ C).