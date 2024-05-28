:- load_library('alice.tuprolog.lib.DCGLibrary').

to_lower(C, LC) :-
    char_code(C, CC),
    (CC >= 65, CC =< 90 ->
        LCC is CC + 32,
        char_code(LC, LCC);
    LC = C).

lookup(K, [(K1, V) | _], V) :- atom_chars(K, [H | _]), to_lower(H, K1).
lookup(K, [_ | T], V) :- lookup(K, T, V).

nonvar(V, _) :- var(V).
nonvar(V, T) :- nonvar(V), call(T).

ws --> [].
ws --> [' '], ws.

check_p([], L) --> [].
check_p([H | T], L) -->
  { member(H, L) },
  [H], check_p(T, L).

op_p(op_add)    --> ['+'].
op_p(op_subtract) --> ['-'].
op_p(op_multiply) --> ['*'].
op_p(op_divide)  --> ['/'].
unop_p(op_negate) --> ['n', 'e', 'g', 'a', 't', 'e'].

unop_p(op_bitnot) --> ['~'].
op_p(op_bitand)  --> ['&'].
op_p(op_bitor)   --> ['|'].
op_p(op_bitxor)  --> ['^'].

terop_first_p(op_bitif) --> ['?'].
terop_first_p(op_bitmux) --> ['¿'].

symbol_p([], _)     --> [].
symbol_p([H | T], List) --> 
 { member(H, List) },
 [H], 
 symbol_p(T, List).


expr_p(variable(Name)) -->
 { nonvar(Name, atom_chars(Name, Chars)) },
 ws, symbol_p(Chars, ['X', 'Y', 'Z', x, y, z]), ws,
 {Chars = [_ | _], atom_chars(Name, Chars)}.


expr_p(const(Value)) -->
  { nonvar(Value, number_chars(Value, Chars)) },
  ws, symbol_p(Chars, ['.', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-']), ws,
  { Chars = [_ | _], 
  \= (Chars, ['-']), 
  number_chars(Value, Chars) }.

  
expr_p(operation(Op, A))     --> ws, unop_p(Op), [' '], ws, expr_p(A), ws.
expr_p(operation(Op, A, B))   --> ws, ['('], ws, expr_p(A), [' '], ws, op_p(Op), [' '] , ws, expr_p(B), ws, [')'], ws.
expr_p(operation(Op, C, T, F))  --> ws, ['('], ws, expr_p(C), [' '], ws, terop_first_p(Op), [' '],  ws, expr_p(T), [' '], ws, [':'], [' '], ws, expr_p(F), ws, [')'], ws.

infix_str(E, A) :- ground(E),
    phrase(expr_p(E), C), atom_chars(A, C), !.

infix_str(E, A) :- atom(A),
    atom_chars(A, C), phrase(expr_p(E), C), !.

evaluate(const(Value), _, Value).
evaluate(variable(Name), VS, V) :-
    lookup(Name, VS, V).

evaluate(operation(OP, A, B, C), VS, R) :-
    evaluate(A, VS, RA),
    evaluate(B, VS, RB),
    evaluate(C, VS, RC),
    op_impl(OP, RA, RB, RC, R).

evaluate(operation(OP, A, B), VS, R) :-
    evaluate(A, VS, RA),
    evaluate(B, VS, RB),
    op_impl(OP, RA, RB, R).

evaluate(operation(OP, A), VS, R) :-
    evaluate(A, VS, RA),
    op_impl(OP, RA, R).

op_impl(op_bitif, A, B, C, R) :- A /\ 1 =:= 1 -> R = B; R = C.
op_impl(op_bitmux, A, B, C, R) :- R is (\A /\ B) \/ (A /\ C).
op_impl(op_add, A, B, R) :- R is A + B.
op_impl(op_subtract, A, B, R) :- R is A - B.
op_impl(op_multiply, A, B, R) :- R is A * B.
op_impl(op_divide, A, B, R) :- B =:= 0 -> R = 0; R is A / B.
op_impl(op_bitand, A, B, R) :- R is A /\ B.
op_impl(op_bitor, A, B, R) :- R is A \/ B.
op_impl(op_bitxor, A, B, R) :- R is (A \/ B) /\ \(A /\ B).

op_impl(op_negate, A, R) :- R is -A.
op_impl(op_bitnot, A, R) :- R is \A.
