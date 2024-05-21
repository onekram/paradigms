init(MX) :- M is round(sqrt(MX)), sieve(2, M, MX), !.

sieve(P, M, MX) :- P > M, !.
sieve(P, M, MX) :- composite(P), P1 is P + 1, sieve(P1, M, MX).
sieve(P, M, MX) :- P1 is P * P, bind_composite(P, P1, MX), P2 is P + 1, sieve(P2, M, MX).

bind_composite(_, N, MX) :- N > MX, !.
bind_composite(S, N, MX) :- assert(composite(N)), N1 is N + S, bind_composite(S, N1, MX).

prime(2).
prime(N) :- N > 2, \+ composite(N).

next_divisor(N, F, F) :- 0 is mod(N,F), !.
next_divisor(N, F, R) :- F1 is F + 1, next_divisor(N, F1, R).

prime_divisors(1, []) :- !.
prime_divisors(N, [N]) :- prime(N), !.
prime_divisors(N, FS) :- number(N), !, prime_divisors(N, 2, FS).
prime_divisors(R, [H1, H2 | T]) :-  H1 =< H2, prime(H1), prime_divisors(R1, [H2 | T]), R is R1 * H1.

prime_divisors(N, _, [N]) :- prime(N), !.
prime_divisors(N, F, [HFS | TFS]) :- next_divisor(N, F, HFS), N1 is div(N, HFS), prime_divisors(N1, HFS, TFS).

intersection(_, [], []) :- !.
intersection([], _, []) :- !. 
intersection([H | TFX], [H | TFY], [H | T]) :- intersection(TFX, TFY, T).
intersection([HFX | TFX], [HFY | TFY], R) :- HFX < HFY, intersection(TFX, [HFY | TFY], R).
intersection([HFX | TFX], [HFY | TFY], R) :- HFX > HFY, intersection([HFX | TFX], TFY, R).

union(FX, [], FX) :- !.
union([], FY, FY) :- !. 
union([H | TFX], [H | TFY], [H | T]) :- union(TFX, TFY, T).
union([HFX | TFX], [HFY | TFY], [HFX | T]) :- HFX < HFY, union(TFX, [HFY | TFY], T).
union([HFX | TFX], [HFY | TFY], [HFY | T]) :- HFX > HFY, union([HFX | TFX], TFY, T).

gcdlcm(X, Y, F, R) :- prime_divisors(X, FX), prime_divisors(Y, FY), G =.. [F, FX, FY, IR], call(G), prime_divisors(R, IR).

gcd(X, Y, R) :- gcdlcm(X, Y, intersection, R).
lcm(X, Y, R) :- gcdlcm(X, Y, union, R).